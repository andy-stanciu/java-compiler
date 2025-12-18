import codegen.platform.isa.ISA;
import codegen.platform.isa.ISAProvider;
import phase.*;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

import static phase.CompilerState.EXIT_FAILURE;
import static phase.CompilerState.EXIT_SUCCESS;

public class Java {
    public static void main(String[] args) {
        var tasks = parseTasks(args);
        if (tasks == null) {
            System.err.println("Usage: Java [-S | -T | -I | -B | -V] <file1.java, file2.java, ...>");
            System.exit(EXIT_FAILURE);
        }

        int status = EXIT_SUCCESS;
        while (!tasks.isEmpty()) {
            var task = tasks.poll();
            final var initialState = CompilerState.builder()
                    .status(status)
                    .sourceFile(task.input)
                    .isa(task.isa)
                    .build();
            final var phases = new CompilerPhaseChain();
            try {
                switch (task.type) {
                    case SCAN -> phases.add(new ScanPhase(true));
                    case TABLE -> phases.add(new ScanPhase())
                            .add(new ParsePhase())
                            .add(new StaticAnalysisPhase(true));
                    case INSTRUCTIONS -> phases.add(new ScanPhase())
                            .add(new ParsePhase())
                            .add(new StaticAnalysisPhase())
                            .add(new DataflowAnalysisPhase(true, false));
                    case BLOCKS -> phases.add(new ScanPhase())
                            .add(new ParsePhase())
                            .add(new StaticAnalysisPhase())
                            .add(new DataflowAnalysisPhase(false, true));
                    case COMPILE -> phases.add(new ScanPhase())
                            .add(new ParsePhase())
                            .add(new StaticAnalysisPhase())
                            .add(new DataflowAnalysisPhase())
                            .add(new CodeGenerationPhase());
                }
                status |= phases.run(initialState).getStatus();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.printf("Unexpected internal compiler error: %s%n", e);
                status = EXIT_FAILURE;
            }
        }

        System.exit(status);
    }

    private static Queue<Task> parseTasks(String[] args) {
        if (args.length == 0) {
            return null;
        }

        Queue<Task> tasks = new LinkedList<>();

        for (int i = 0; i < args.length; i++) {
            TaskType type;
            ISA isa = ISAProvider.getISA_x86_64();  // default isa
            int j;

            if (args[i].charAt(0) == '-') {  // operator found
                j = i + 1;
                String operator = args[i];
                switch (operator.toLowerCase()) {
                    case "-s", "--scan" -> type = TaskType.SCAN;
                    case "-t", "--table" -> type = TaskType.TABLE;
                    case "-i", "--instructions" -> type = TaskType.INSTRUCTIONS;
                    case "-b", "--blocks" -> type = TaskType.BLOCKS;
                    case "-v", "--version" -> {
                        type = TaskType.COMPILE;
                        String version = args[j++];
                        switch (version.toLowerCase()) {
                            case "x86_64" -> isa = ISAProvider.getISA_x86_64();
                            case "arm64" -> isa = ISAProvider.getISA_arm64();
                            default -> {
                                System.err.printf("Invalid version: %s. Supported versions: x86_64, arm64, ...%n", version);
                                return null;
                            }
                        }
                    }
                    default -> {
                        System.err.printf("Unrecognized operand: %s%n", operator);
                        return null;
                    }
                }
            } else {  // no operands => we want to compile everything
                j = i;
                type = TaskType.COMPILE;
            }

            boolean foundValidArgs = false;
            while (j < args.length && args[j].charAt(0) != '-') {  // while not an operand
                String path = args[j];
                var f = new File(path);

                if (f.isDirectory()) {
                    // read all files within directory
                    var children = f.listFiles();
                    if (children == null) {
                        System.err.printf("Empty directory: %s%n", path);
                        return null;
                    }

                    for (var child : children) {
                        if (child.isFile() && child.getName().endsWith(".java")) {
                            if (!child.canRead()) {
                                System.err.printf("Cannot read file: %s%n", child.getPath());
                                continue;
                            }

                            tasks.offer(new Task(type, child, isa));
                            foundValidArgs = true;
                        }
                    }
                } else {  // file
                    if (!f.canRead()) {
                        System.err.printf("Cannot read file: %s%n", path);
                        return null;
                    }

                    tasks.offer(new Task(type, f, isa));
                    foundValidArgs = true;
                }

                if (!foundValidArgs) {  // no input files found associated with operand
                    return null;
                }

                i = ++j - 1;
            }
        }

        return tasks;
    }

    private record Task(TaskType type, File input, ISA isa) {}

    private enum TaskType {
        SCAN,          // scan
        TABLE,         // scan, parse, static semantic analysis, and print symbol tables
        INSTRUCTIONS,  // scan, parse, static semantic analysis, dataflow analysis, and print instruction graph
        BLOCKS,        // scan, parse, static semantic analysis, dataflow analysis, and print block graph
        COMPILE        // scan, parse, static semantic analysis, dataflow analysis, code generation
    }
}
