#!/usr/bin/env python3
from __future__ import annotations

import os
from concurrent.futures import ThreadPoolExecutor, as_completed
import argparse
import difflib
import shutil
import subprocess
from dataclasses import dataclass
from pathlib import Path
from typing import Optional

# -----------------------------
# Constants (edit if needed)
# -----------------------------
PATH_TO_JAVA_FILES = Path("test/resources/CodeGen") # contains src/*.java
PATH_TO_BOOT_C_DIR = Path("src/runtime")            # contains boot.c

# Your compiler invocation (same as the bash script)
COMPILER_CMD = ["java", "-cp", "build/classes:lib/*", "Java"]

# Toolchain
CLANG = "clang"
ROSETTA_RUN = ["arch", "-x86_64"]  # run x86_64 binary via Rosetta on Apple Silicon

# Output layout
OUT_DIR = PATH_TO_JAVA_FILES / "out"
ASM_DIR = OUT_DIR / "asm"
NATIVE_DIR = OUT_DIR / "native"
REF_DIR = OUT_DIR / "ref"
DIFF_DIR = OUT_DIR / "diffs"

def chunked(xs: list[Path], n: int) -> list[list[Path]]:
  return [xs[i:i+n] for i in range(0, len(xs), n)]

def run_chunk(files: list[Path], boot_c: Path) -> list[TestResult]:
  return [build_and_run_one(jf, boot_c) for jf in files]

# -----------------------------
# Pretty output (optional)
# -----------------------------
try:
  from rich.console import Console
  from rich.progress import Progress, SpinnerColumn, BarColumn, TextColumn, TimeElapsedColumn
  from rich.table import Table
except Exception:
  Console = None  # type: ignore

console = Console() if Console else None


def log(msg: str) -> None:
  if console:
    console.print(msg)
  else:
    print(msg)


def run(cmd: list[str], cwd: Optional[Path] = None) -> subprocess.CompletedProcess:
  # capture_output=True captures stdout+stderr; text=True returns strings.
  return subprocess.run(cmd, cwd=str(cwd) if cwd else None, capture_output=True, text=True)


def ensure_empty_dir(p: Path) -> None:
  if p.exists():
    shutil.rmtree(p)
  p.mkdir(parents=True, exist_ok=True)

@dataclass
class TestResult:
  name: str
  ok: bool
  native_rc: int
  ref_rc: int
  diff_path: Optional[Path] = None
  note: str = ""


def build_and_run_one(java_file: Path, boot_c: Path) -> TestResult:
  name = java_file.stem

  asm_path = ASM_DIR / f"{name}.S"
  native_exe = NATIVE_DIR / name
  ref_classes = REF_DIR / name

  # 1) Compile Java -> assembly using your compiler
  cp = run(COMPILER_CMD + [str(java_file)])
  if cp.returncode != 0:
    return TestResult(
      name=name,
      ok=False,
      native_rc=999,
      ref_rc=999,
      note=f"Compiler failed (rc={cp.returncode}). stderr:\n{cp.stderr}",
    )
  asm_path.write_text(cp.stdout, encoding="utf-8")

  # 2) Build native x86_64 binary: clang -arch x86_64 boot.c foo.S -o exe
  ensure_empty_dir(ref_classes)
  build = run([
    CLANG, "-arch", "x86_64",
    str(boot_c),
    str(asm_path),
    "-o", str(native_exe),
  ])
  if build.returncode != 0:
    return TestResult(
      name=name,
      ok=False,
      native_rc=build.returncode,
      ref_rc=999,
      note=f"Native build failed. stderr:\n{build.stderr}",
    )

  # 3) Run native under Rosetta
  native = run(ROSETTA_RUN + [str(native_exe)])

  # 4) Reference: run Java source file directly
  ref = run(["java", str(java_file)])

  native_out = native.stdout.replace("\r\n", "\n")
  ref_out = ref.stdout.replace("\r\n", "\n")

  ok = (native_out == ref_out)

  diff_path = None
  note = ""
  if not ok:
    diff_path = DIFF_DIR / f"{name}.diff"
    a = ref_out.splitlines(keepends=True)
    b = native_out.splitlines(keepends=True)
    delta = difflib.unified_diff(
      a, b,
      fromfile="ref(java file.java).stdout",
      tofile="native(rosetta).stdout",
      lineterm="\n",
    )
    diff_path.write_text("".join(delta), encoding="utf-8")

    # Keep rc info as a hint, but don't make it a failure condition.
    if ref.returncode != 0 or native.returncode != 0:
      note = f"Stdout mismatch (native_rc={native.returncode}, ref_rc={ref.returncode})"
    else:
      note = "Stdout mismatch"

  return TestResult(
    name=name,
    ok=ok,
    native_rc=native.returncode,
    ref_rc=ref.returncode,
    diff_path=diff_path,
    note=note,
  )


def main() -> int:
  ap = argparse.ArgumentParser(description="Local compiler tester: compiler -> asm -> native vs JVM reference")
  ap.add_argument(
    "sources",
    nargs="*",
    help="Optional: one or more test base names (e.g., Foo Bar for Foo.java Bar.java). If omitted, runs all tests.",
  )

  args = ap.parse_args()

  boot_c = PATH_TO_BOOT_C_DIR / "boot.c"
  if not boot_c.exists():
    log(f"[red]Missing boot.c at {boot_c}[/red]" if console else f"Missing boot.c at {boot_c}")
    return 2

  # Prepare output dirs
  ensure_empty_dir(OUT_DIR)
  ASM_DIR.mkdir(parents=True, exist_ok=True)
  NATIVE_DIR.mkdir(parents=True, exist_ok=True)
  REF_DIR.mkdir(parents=True, exist_ok=True)
  DIFF_DIR.mkdir(parents=True, exist_ok=True)

  src_dir = PATH_TO_JAVA_FILES / "src"
  if not src_dir.exists():
    log(f"[red]Missing test src dir: {src_dir}[/red]" if console else f"Missing test src dir: {src_dir}")
    return 2

  if args.sources:
    java_files = [src_dir / f"{s}.java" for s in args.sources]
  else:
    java_files = sorted(src_dir.glob("*.java"))

  missing = [p for p in java_files if not p.exists()]
  if missing:
    for p in missing:
      log(f"[red]Missing: {p}[/red]" if console else f"Missing: {p}")
    return 2

  results: list[TestResult] = []

  chunks = chunked(java_files, 10)  # <= 10 files per thread

  # number of worker threads: one per chunk, capped (and at least 1)
  max_workers = max(1, min(len(chunks), (os.cpu_count() or 4)))

  if console:
    progress = Progress(
      SpinnerColumn(),
      TextColumn("[progress.description]{task.description}"),
      BarColumn(),
      TimeElapsedColumn(),
      console=console,
    )
    with progress:
      task = progress.add_task("Running tests", total=len(java_files))

      with ThreadPoolExecutor(max_workers=max_workers) as ex:
        futures = [ex.submit(run_chunk, ch, boot_c) for ch in chunks]
        for fut in as_completed(futures):
          chunk_results = fut.result()
          results.extend(chunk_results)
          progress.advance(task, advance=len(chunk_results))
  else:
    with ThreadPoolExecutor(max_workers=max_workers) as ex:
      futures = [ex.submit(run_chunk, ch, boot_c) for ch in chunks]
      for fut in as_completed(futures):
        results.extend(fut.result())

  # Report
  passed = sum(1 for r in results if r.ok)
  failed = len(results) - passed

  if console:
    table = Table(title="Test results")
    table.add_column("Test")
    table.add_column("Status")
    table.add_column("Note")
    table.add_column("Diff (if any)")
    for r in results:
      status = "[green]PASS[/green]" if r.ok else "[red]FAIL[/red]"
      diff = str(r.diff_path) if r.diff_path else ""
      table.add_row(r.name, status, r.note, diff)
    console.print(table)
    console.print(f"\nOverall: {passed}/{len(results)} passed, {failed} failed")
  else:
    for r in results:
      print(f"{r.name}: {'PASS' if r.ok else 'FAIL'} {('- ' + r.note) if r.note else ''}")
      if r.diff_path:
        print(f"  diff: {r.diff_path}")
    print(f"Overall: {passed}/{len(results)} passed, {failed} failed")

  return 0 if failed == 0 else 1


if __name__ == "__main__":
  raise SystemExit(main())
