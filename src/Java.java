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
    public static void main(String[] args) {
        var tasks = parseTasks(args);
        if (tasks == null) {
            System.err.println("Usage: Java [-S | -P | -A | -T | -D] <file1.java, file2.java, ...>");
            System.exit(1);
        }

        int status = 0;
        while (!tasks.isEmpty()) {
            var task = tasks.poll();
            switch (task.type) {
                case SCAN -> status |= scan(task);
                case PRETTY_PRINT -> status |= parse(task, new PrettyPrintVisitor());
                case AST -> status |= parse(task, new ASTVisitor());
                case TABLE -> status |= validate(task);
                case DATAFLOW -> status |= dataflow(task);
                case COMPILE -> status |= compile(task);
            }
        }

        System.exit(status);
    }

    private static int scan(Task task) {
        int status = 0;
        try {
            var sf = new ComplexSymbolFactory();
            var in = new BufferedReader(new InputStreamReader(new FileInputStream(task.input)));

            var s = new scanner(in, sf);
            var t = s.next_token();

            while (t.sym != sym.EOF) {
                // print each token that we scan
                System.out.print(s.symbolToString(t) + " ");

                if (t.sym == sym.error) {
                    status = 1;
                }

                t = s.next_token();
            }
        } catch (Exception e) {
            System.err.println("Unexpected internal compiler error: " + e);
            e.printStackTrace();
            status = 1;
        }

        return status;
    }

    private static int parse(Task task, Visitor visitor) {
        int status = 0;
        try {
            var sf = new ComplexSymbolFactory();
            var in = new BufferedReader(new InputStreamReader(new FileInputStream(task.input)));

            var s = new scanner(in, sf);
            var p = new parser(s, sf);
            var root = p.parse();

            var program = (Program) root.value;
            program.accept(visitor);
        } catch (Exception e) {
            System.err.println("Unexpected internal compiler error: " + e);
            e.printStackTrace();
            status = 1;
        }

        return status;
    }

    private static int validate(Task task) {
        int status = 0;
        try {
            var sf = new ComplexSymbolFactory();
            var in = new BufferedReader(new InputStreamReader(new FileInputStream(task.input)));

            var logger = Logger.getInstance();
            logger.start(task.input.getName());

            var s = new scanner(in, sf);
            var p = new parser(s, sf);
            var root = p.parse();

            var program = (Program) root.value;
            var symbolContext = SymbolContext.create();
            program.accept(new GlobalVisitor(symbolContext));
            program.accept(new ClassVisitor(symbolContext));
            program.accept(new LocalVisitor(symbolContext));

            // dump symbol tables
            symbolContext.dump();

            if (logger.hasError()) {
                status = 1;
                System.err.printf("Static semantic analysis found %d error(s), %d warning(s)%n",
                        logger.getErrorCount(), logger.getWarningCount());
            }
        } catch (Exception e) {
            System.err.println("Unexpected internal compiler error: " + e);
            e.printStackTrace();
            status = 1;
        }

        return status;
    }

    private static int dataflow(Task task) {
        int status = 0;
        try {
            var sf = new ComplexSymbolFactory();
            var in = new BufferedReader(new InputStreamReader(new FileInputStream(task.input)));

            var logger = Logger.getInstance();
            logger.start(task.input.getName());

            var s = new scanner(in, sf);
            var p = new parser(s, sf);
            var root = p.parse();

            var program = (Program) root.value;
            var symbolContext = SymbolContext.create();
            program.accept(new GlobalVisitor(symbolContext));
            program.accept(new ClassVisitor(symbolContext));
            program.accept(new LocalVisitor(symbolContext));

            if (logger.hasError()) {
                status = 1;
                System.err.printf("Static semantic analysis found %d error(s), %d warning(s)%n",
                        logger.getErrorCount(), logger.getWarningCount());
            } else {
                program.accept(new DataflowVisitor());
            }
        } catch (Exception e) {
            System.err.println("Unexpected internal compiler error: " + e);
            e.printStackTrace();
            status = 1;
        }

        return status;
    }

    private static int compile(Task task) {
        int status = 0;
        try {
            var sf = new ComplexSymbolFactory();
            var in = new BufferedReader(new InputStreamReader(new FileInputStream(task.input)));

            var logger = Logger.getInstance();
            logger.start(task.input.getName());

            var s = new scanner(in, sf);
            var p = new parser(s, sf);
            var root = p.parse();

            var program = (Program) root.value;
            var symbolContext = SymbolContext.create();
            program.accept(new GlobalVisitor(symbolContext));
            program.accept(new ClassVisitor(symbolContext));
            program.accept(new LocalVisitor(symbolContext));

            if (!logger.hasError()) {  // generate code only if no error
                program.accept(new CodeDataVisitor(symbolContext));
                program.accept(new CodeGenVisitor(symbolContext));
            } else {
                status = 1;
                System.err.printf("Static semantic analysis found %d error(s), %d warning(s)%n",
                        logger.getErrorCount(), logger.getWarningCount());
            }
        } catch (Exception e) {
            System.err.println("Unexpected internal compiler error: " + e);
            e.printStackTrace();
            status = 1;
        }

        return status;
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
                    case "-d", "--dataflow" -> type = TaskType.DATAFLOW;
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
        DATAFLOW,      // scan, parse, static semantic analysis, and dataflow analysis
        COMPILE        // scan, parse, static semantic analysis, code generation
    }
}
