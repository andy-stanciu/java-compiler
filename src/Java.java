import ast.Program;
import ast.visitor.ASTVisitor;
import ast.visitor.PrettyPrintVisitor;
import ast.visitor.Visitor;
import ast.visitor.codegen.CodeDataVisitor;
import ast.visitor.codegen.CodeGenVisitor;
import ast.visitor.dataflow.DataflowVisitor;
import ast.visitor.semantics.ClassVisitor;
import ast.visitor.semantics.GlobalVisitor;
import ast.visitor.semantics.LocalVisitor;
import codegen.platform.isa.ISA;
import codegen.platform.isa.ISAProvider;
import parser.parser;
import scanner.*;
import parser.sym;
import java_cup.runtime.ComplexSymbolFactory;
import semantics.Logger;
import semantics.table.SymbolContext;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class Java {
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_FAILURE = 1;
    private static Logger logger;
    private static ComplexSymbolFactory symbolFactory;
    private static scanner scanner;
    private static Program ast;
    private static SymbolContext symbolContext;
    private static ISA isa;
    private static DataflowVisitor dataflowVisitor;  // TODO: move data structures to separate class

    public static void main(String[] args) {
        var tasks = parseTasks(args);
        if (tasks == null) {
            System.err.println("Usage: Java [-S | -P | -A | -T | -I | -B | -V] <file1.java, file2.java, ...>");
            System.exit(EXIT_FAILURE);
        }

        int status = EXIT_SUCCESS;
        while (!tasks.isEmpty()) {
            var task = tasks.poll();
            try {
                switch (task.type) {
                    case SCAN -> status |= scanTask(task);
                    case PRETTY_PRINT -> status |= parseTask(task, new PrettyPrintVisitor());
                    case AST -> status |= parseTask(task, new ASTVisitor());
                    case TABLE -> status |= doStaticAnalysisTask(task);
                    case INSTRUCTIONS -> status |= doInstructionsTask(task);
                    case BLOCKS -> status |= doBlocksTask(task);
                    case COMPILE -> status |= compile(task);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.printf("Unexpected internal compiler error: %s%n", e);
                status = EXIT_FAILURE;
            }
        }

        System.exit(status);
    }

    private static int scanTask(Task task) throws Exception {
        int status = scan(task);

        var t = scanner.next_token();
        while (t.sym != sym.EOF) {
            System.out.print(scanner.symbolToString(t) + " ");
            if (t.sym == sym.error) {
                status = EXIT_FAILURE;
            }
            t = scanner.next_token();
        }

        return status;
    }

    private static int scan(Task task) throws IOException {
        symbolFactory = new ComplexSymbolFactory();
        var in = new BufferedReader(new InputStreamReader(new FileInputStream(task.input)));
        scanner = new scanner(in, symbolFactory);

        return EXIT_SUCCESS;
    }

    private static int parseTask(Task task, Visitor visitor) throws Exception {
        int status = parse(task);
        ast.accept(visitor);
        return status;
    }

    private static int parse(Task task) throws Exception {
        int status = scan(task);
        if (status == EXIT_FAILURE) return status;

        var p = new parser(scanner, symbolFactory);
        ast = (Program) p.parse().value;

        return status;
    }

    private static int doStaticAnalysisTask(Task task) throws Exception {
        int status = doStaticAnalysis(task);
        symbolContext.dump();
        return status;
    }

    private static int doStaticAnalysis(Task task) throws Exception {
        int status = parse(task);
        if (status == EXIT_FAILURE) return status;

        logger = Logger.getInstance();
        logger.start(task.input.getName());
        symbolContext = SymbolContext.create();

        ast.accept(new GlobalVisitor(symbolContext));
        ast.accept(new ClassVisitor(symbolContext));
        ast.accept(new LocalVisitor(symbolContext));

        if (logger.hasError()) {
            status = EXIT_FAILURE;
            System.err.printf("Static semantic analysis found %d error(s), %d warning(s)%n",
                    logger.getErrorCount(), logger.getWarningCount());
        }

        return status;
    }

    private static int doDataflowAnalysis(Task task) throws Exception {
        int status = doStaticAnalysis(task);
        if (status == EXIT_FAILURE) return status;

        logger.restart();
        dataflowVisitor = new DataflowVisitor(symbolContext);
        ast.accept(dataflowVisitor);

        if (logger.hasError()) {
            status = EXIT_FAILURE;
            System.err.printf("Dataflow analysis found %d error(s), %d warning(s)%n",
                    logger.getErrorCount(), logger.getWarningCount());
        }

        return status;
    }

    private static int doInstructionsTask(Task task) throws Exception {
        int status = doDataflowAnalysis(task);
        dataflowVisitor.dumpInstructions();
        return status;
    }

    private static int doBlocksTask(Task task) throws Exception {
        int status = doDataflowAnalysis(task);
        dataflowVisitor.dumpBlocks();
        return status;
    }

    private static int compile(Task task) throws Exception {
        int status = doDataflowAnalysis(task);
        if (status == EXIT_FAILURE) return status;

        ast.accept(new CodeDataVisitor(symbolContext, isa));
        ast.accept(new CodeGenVisitor(symbolContext, isa));

        return EXIT_SUCCESS;
    }

    private static Queue<Task> parseTasks(String[] args) {
        if (args.length == 0) {
            return null;
        }

        Queue<Task> tasks = new LinkedList<>();

        for (int i = 0; i < args.length; i++) {
            TaskType type;
            int j;

            if (args[i].charAt(0) == '-') {  // operator found
                j = i + 1;
                String operator = args[i];
                switch (operator.toLowerCase()) {
                    case "-s", "--scan" -> type = TaskType.SCAN;
                    case "-p", "--pretty-print" -> type = TaskType.PRETTY_PRINT;
                    case "-a", "--ast" -> type = TaskType.AST;
                    case "-t", "--table" -> type = TaskType.TABLE;
                    case "-i", "--instructions" -> type = TaskType.INSTRUCTIONS;
                    case "-b", "--blocks" -> type = TaskType.BLOCKS;
                    case "-v", "--version" -> {
                        type = TaskType.COMPILE;
                        String version = args[j++];
                        switch (version.toLowerCase()) {
                            case "x86_64" -> isa = ISAProvider.getISA_x86_64();
                            default -> {
                                System.err.printf("Invalid version: %s. Supported versions: x86_64, ...%n", version);
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
                isa = ISAProvider.getISA_x86_64();  // default isa
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

                            tasks.offer(new Task(type, child));
                            foundValidArgs = true;
                        }
                    }
                } else {  // file
                    if (!f.canRead()) {
                        System.err.printf("Cannot read file: %s%n", path);
                        return null;
                    }

                    tasks.offer(new Task(type, f));
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

    private record Task(TaskType type, File input) {}

    private enum TaskType {
        SCAN,          // scan
        PRETTY_PRINT,  // scan, parse, and pretty-print
        AST,           // scan, parse, and print ast
        TABLE,         // scan, parse, static semantic analysis, and print symbol tables
        INSTRUCTIONS,  // scan, parse, static semantic analysis, dataflow analysis, and print instruction graph
        BLOCKS,        // scan, parse, static semantic analysis, dataflow analysis, and print block graph
        COMPILE        // scan, parse, static semantic analysis, code generation
    }
}
