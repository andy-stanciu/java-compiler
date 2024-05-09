import ast.Program;
import ast.visitor.ASTVisitor;
import ast.visitor.PrettyPrintVisitor;
import ast.visitor.Visitor;
import ast.visitor.semantics.ClassVisitor;
import ast.visitor.semantics.GlobalVisitor;
import ast.visitor.semantics.LocalVisitor;
import parser.parser;
import scanner.*;
import parser.sym;
import java_cup.runtime.ComplexSymbolFactory;
import semantics.Logger;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class MiniJava {
    public static void main(String[] args) {
        var tasks = parseTasks(args);
        if (tasks == null) {
            System.err.println("Usage: MiniJava -S | -P | -A | -T <file1.java, file2.java, ...>");
            System.exit(1);
        }

        int status = 0;
        while (!tasks.isEmpty()) {
            Task task = tasks.poll();
            switch (task.type) {
                case SCAN -> status |= scan(task);
                case PRETTY_PRINT -> status |= parse(task, new PrettyPrintVisitor());
                case AST -> status |= parse(task, new ASTVisitor());
                case TABLE -> status |= validate(task);
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
            program.accept(new GlobalVisitor());
            program.accept(new ClassVisitor());
            program.accept(new LocalVisitor());

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

    private static Queue<Task> parseTasks(String[] args) {
        if (args.length == 0) {
            return null;
        }

        Queue<Task> tasks = new LinkedList<>();

        for (int i = 0; i < args.length; i++) {
            String operator = args[i];

            TaskType type;
            switch (operator.toLowerCase()) {
                case "-s", "--scan" -> type = TaskType.SCAN;
                case "-p", "--pretty-print" -> type = TaskType.PRETTY_PRINT;
                case "-a", "--ast" -> type = TaskType.AST;
                case "-t", "--table" -> type = TaskType.TABLE;
                default -> {
                    System.err.printf("Unrecognized operand: %s%n", operator);
                    return null;
                }
            }

            int j = i + 1;
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
        TABLE          // scan, parse, static semantic analysis, and print symbol tables
    }
}
