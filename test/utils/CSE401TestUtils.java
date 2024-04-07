import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/***
 * A collection of utility methods for writing JUnit tests.
 *
 * @author Apollo Zhu
 * @author Henry Heino
 */
public final class CSE401TestUtils {
    /**
     * @deprecated you don't need to construct an instance of
     * {@code CSE401TestUtils}.
     */
    @Deprecated(forRemoval = true)
    private CSE401TestUtils() {
    }

    /**
     * Number of seconds before exec stops executing the specified command
     * to throw an AssertionError.
     */
    public static final long EXEC_WAIT_TIMEOUT_SECONDS = 30;

    // region Accessing Test Resources

    private static final String ROOT_RELATIVE_RESOURCES = "test/resources/";

    private static final String ROOT_RELATIVE_SAMPLE_MINI_JAVA_PROGRAMS
            = "SamplePrograms/SampleMiniJavaPrograms/";

    /**
     * Returns the correct path to the {@code test/resources/} folder to
     * allow JUnit tests to be run either through IDEs or through {@code ant}.
     * <p>
     * For example, the {@code TestDemoLanguageScanner} test suite should define
     * {@code TEST_FILES_LOCATION} like this:
     *
     * <pre>{@code
     * public static final String TEST_FILES_LOCATION = CSE401TestUtils.getResourcesPath() + "DemoLanguageScanner/";
     * }</pre>
     *
     * @return the relative path to the {@code test/resources/} directory.
     */
    public static String getResourcesPath() {
        return _fixPath(Path.of(ROOT_RELATIVE_RESOURCES)).toString() + "/";
    }

    /**
     * Returns the correct path to the SampleMiniJavaPrograms folder to
     * allow JUnit tests to be run either through IDEs or through {@code ant}.
     *
     * @return the relative path to the SampleMiniJavaPrograms directory.
     */
    public static String getSampleMiniJavaProgramsPath() {
        return _fixPath(Path.of(ROOT_RELATIVE_SAMPLE_MINI_JAVA_PROGRAMS)).toString() + "/";
    }

    /**
     * Detects if resource finding should start from project root and not the
     * {@code test/} folder.
     *
     * @return true if running JUnit through ant and should find test resources
     * with current working directory being the project root, or false if
     * running JUnit through an IDE and should look for test resources with
     * current working directory being the {@code test/} folder.
     */
    public static boolean shouldUseRootRelativePath() {
        return Files.exists(Path.of(ROOT_RELATIVE_RESOURCES));
    }

    static Path _fixPath(Path path) {
        // Only attempt to fix path if we might potentially run into issues.
        if (shouldUseRootRelativePath()) return path;
        // Don't fix path if it already exists
        if (Files.exists(path)) return path;
        if (path.isAbsolute()) return path;
        // Return fixed path if fixed successfully
        Path maybePath = Path.of("..").resolve(path);
        if (Files.exists(maybePath)) return maybePath;
        // Otherwise, return the original
        return path;
    }

    // endregion

    // region Capture Output Streams

    /**
     * Collects and returns the contents printed to the system out and system
     * error streams when running a piece of code that doesn't call
     * {@code System.exit}.
     * <p><br>
     * For example, you can assert that the following code doesn't print to
     * system error:
     *
     * <pre>{@code
     * PrintStreams printed = CSE401TestUtils.collectPrinted(() -> {
     *     // your code goes here...
     *     runScannerTestCase("SimpleArithmetic");
     * });
     * assertTrue(printed.systemErr().isEmpty());
     * }</pre>
     * <p><br>
     * You can also check that the contents printed match the expected:
     *
     * <pre>{@code
     * PrintStreams printed = CSE401TestUtils.collectPrinted(() -> {
     *     // System.in is "1"
     *     runScannerTestCase(...);
     * });
     * assertEquals("<UNEXPECTED(1)> ", printed.systemOut());
     * assertEquals("\nUnexpected character '1' on line 1 at column 1 of input.\n", printed.systemErr());
     * }</pre>
     *
     * @param code     the code run that writes to standard output
     *                 and/or system error and doesn't call
     *                 {@code System.exit}.
     * @param <Thrown> exception potentially thrown by the given code.
     * @return A PrintStreams object containing the printed contents of
     * system out and system error while executing the given code.
     * @throws Thrown if the given code throws an error, then such exception
     *                is thrown as is.
     * @see #collectSystemOut(ThrowingRunnable)
     * @see #collectSystemErr(ThrowingRunnable)
     * @see #runCatchingExit(Runnable)
     */
    public static <Thrown extends Throwable>
    PrintStreams collectPrinted(ThrowingRunnable<Thrown> code) throws Thrown {
        AtomicReference<String> err = new AtomicReference<>("");
        String out = collectSystemOut(() -> err.set(collectSystemErr(code)));
        return new PrintStreams(out, err.get());
    }

    private static <Thrown extends Throwable>
    String collectSystemOut(ThrowingRunnable<Thrown> code) throws Thrown {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream defaultOut = System.out;
        try {
            System.setOut(new PrintStream(out));
            code.run();
            return out.toString();
        } finally {
            System.setOut(defaultOut);
        }
    }

    private static <Thrown extends Throwable>
    String collectSystemErr(ThrowingRunnable<Thrown> code) throws Thrown {
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream defaultErr = System.err;
        try {
            System.setErr(new PrintStream(err));
            code.run();
            return err.toString();
        } finally {
            System.setErr(defaultErr);
        }
    }

    // endregion

    // region Capture Exit Status

    /**
     * Run the given piece of code until {@code System.exit} is called,
     * and returns the exit status code, output, and error the code produced.
     *
     * <pre>{@code
     * ExecutionResult result = CSE401TestUtils.runCatchingExit(() -> {
     *     MiniJava.main(new String[]{ "-S", "filename.java" });
     * });
     * assertEquals(0, result.exitStatus());
     * assertEquals("...", result.systemOut());
     * assertEquals("...", result.systemErr());
     * }</pre>
     * <p>
     * If you are running JUnit tests through IntelliJ IDEA,
     * <ol>
     *     <li>From the main menu, select, select Run | Edit Configurations</li>
     *     <li>Add {@code -Djava.security.manager=allow} to VM options</li>
     * </ol>
     * <p>
     * Now, your VM options should look something like
     * {@code -ea -Djava.security.manager=allow}.
     * <p><br>
     * Note: it is expected that you'll see the following warnings:
     * <pre>
     * WARNING: A terminally deprecated method in java.lang.System has been called
     * WARNING: System::setSecurityManager has been called by CSE401TestUtils
     * WARNING: Please consider reporting this to the maintainers of CSE401TestUtils
     * WARNING: System::setSecurityManager will be removed in a future release
     * </pre>
     *
     * @param code the code to be run that calls {@code System.exit}.
     * @return the execution result containing the exit status and contents
     * printed to standard output and standard error during the execution.
     * @see <a href="https://www.jetbrains.com/help/idea/run-debug-configuration.html">Run/debug configurations</a>
     * in IntelliJ IDEA.
     */
    public static ExecutionResult runCatchingExit(Runnable code) {
        AtomicInteger exitStatus = new AtomicInteger();
        PrintStreams printed = CSE401TestUtils.collectPrinted(()
                -> exitStatus.set(CSE401TestUtils.getExitStatus(code::run)));
        return new ExecutionResult(exitStatus.get(), printed);
    }

    @SuppressWarnings("removal")
    private static <Thrown extends Throwable>
    int getExitStatus(ThrowingRunnable<Thrown> code) throws Thrown {
        SecurityManager securityManager = System.getSecurityManager();
        allow catchExit = new allow();
        catchExit.activate();

        try {
            System.setSecurityManager(catchExit);
        } catch (UnsupportedOperationException exception) {
            fail("need to add -Djava.security.manager=allow to VM options.");
        }
        int status = 0;
        try {
            code.run();
        } catch (allow.ExitException exit) {
            status = exit.getExitStatus();
        } finally {
            System.setSecurityManager(securityManager);
        }

        return status;
    }

    // endregion

    // region Execute Command Line Commands

    /**
     * Compiles the specified MiniJava source file using the MiniJava compiler,
     * links it against gcc to produce an executable, and runs the compiled
     * program to collect its execution result.
     *
     * @param pathToMiniJavaSourceFile       path to the MiniJava source code to
     *                                       be compiled.
     * @param argsForCompiledMiniJavaProgram arguments to pass to the compiled
     *                                       MiniJava program.
     * @return the exit status, and printed contents in {@code System.out} and
     * {@code System.err}.
     * @throws IOException          if the given MiniJava source file
     *                              can't be read.
     * @throws InterruptedException if either the MiniJava compiler, gcc, or the
     *                              compiled program didn't finish running in
     *                              reasonable time.
     */
    public static ExecutionResult compileAndRunWithMiniJava(
            Path pathToMiniJavaSourceFile,
            String... argsForCompiledMiniJavaProgram
    ) throws IOException, InterruptedException {
        String filename = pathToMiniJavaSourceFile.getFileName().toString();

        // Temp files for output/asm.
        // https://stackoverflow.com/questions/26860167/what-is-a-safe-way-to-create-a-temp-file-in-java
        File asmFileObj = File.createTempFile("testasm-" + filename + "-", ".s");
        File outputFileObj = File.createTempFile("outfile-" + filename + "-", ".out");
        // FIXME: do we want to delete the files after done?
        // asmFileObj.deleteOnExit();
        // outputFileObj.deleteOnExit();
        // FIXME: surface these paths for debugging tests?
        String asmFile = asmFileObj.getAbsolutePath();
        String outputFile = outputFileObj.getAbsolutePath();

        // Compile MiniJava code using the MiniJava compiler to assembly
        String root = CSE401TestUtils.shouldUseRootRelativePath() ? "." : "..";
        ExecutionResult compileResult = CSE401TestUtils.exec(
                "java",
                "-cp", String.format("%s/build/classes%s%s/lib/java-cup-11b.jar", root, System.getProperty("path.separator"), root),
                "MiniJava",
                pathToMiniJavaSourceFile.toString()
        );
        assertEquals("MiniJava compiler failed to compile " + pathToMiniJavaSourceFile + ".",
                "", compileResult.systemErr());
        assertEquals("MiniJava compiler didn't exit with status 0.",
                0, compileResult.exitStatus());
        assertFalse("MiniJava compiler should print assembly to standard output, but is empty.",
                compileResult.systemOut().isEmpty());
        Files.writeString(asmFileObj.toPath(), compileResult.systemOut());

        // Produce executable file using gcc
        ExecutionResult linkResult = CSE401TestUtils.exec(
                "gcc",
                "-o", outputFile,
                asmFile,
                String.format("%s/src/runtime/boot.c", root)
        );
        assertEquals("gcc failed with errors.",
                "", linkResult.systemErr());
        assertEquals("gcc should print nothing to standard output.",
                "", linkResult.systemOut());
        assertEquals("gcc didn't exit with status 0.",
                0, linkResult.exitStatus());

        // Run the compiled MiniJava program
        List<String> argsForExec = new ArrayList<>();
        argsForExec.add(outputFile);
        argsForExec.addAll(Arrays.asList(argsForCompiledMiniJavaProgram));
        return exec(argsForExec);
    }

    /**
     * Compiles the specified MiniJava source file using Java, runs the
     * compiled program, and returns the execution result for the entire
     * process.
     *
     * @param pathToMiniJavaSourceFile       path to the MiniJava source code to
     *                                       be compiled.
     * @param argsForCompiledMiniJavaProgram arguments to pass to the compiled
     *                                       MiniJava program.
     * @return the exit status, and printed contents in {@code System.out} and
     * {@code System.err}.
     * @throws IOException          if the given MiniJava source file
     *                              can't be read.
     * @throws InterruptedException if either Java or the compiled program
     *                              didn't finish running in reasonable time.
     */
    public static ExecutionResult compileAndRunWithJava(
            Path pathToMiniJavaSourceFile,
            String... argsForCompiledMiniJavaProgram
    ) throws IOException, InterruptedException {
        List<String> argsForExec = new ArrayList<>();
        argsForExec.add("java");
        argsForExec.add(pathToMiniJavaSourceFile.toString());
        argsForExec.addAll(Arrays.asList(argsForCompiledMiniJavaProgram));
        return exec(argsForExec);
    }

    private static ExecutionResult exec(String... args)
            throws IOException, InterruptedException {
        return exec(List.of(args));
    }

    private static ExecutionResult exec(List<String> args)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder(args).start();
        // yes, input stream contains contents of stdout
        String systemOut = new String(process.getInputStream().readAllBytes());
        String systemErr = new String(process.getErrorStream().readAllBytes());
        boolean exited = process.waitFor(EXEC_WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(
                "exec didn't finish in " + EXEC_WAIT_TIMEOUT_SECONDS + " seconds.",
                exited);
        return new ExecutionResult(process.exitValue(), systemOut, systemErr);
    }

    // endregion
}
