import AST.Program;
import AST.Visitor.ASTVisitor;
import AST.Visitor.PrettyPrintVisitor;
import AST.Visitor.Visitor;
import Parser.parser;
import Scanner.*;
import Parser.sym;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class MiniJava {
    public static void main(String[] args) {
        Queue<Task> tasks = parseTasks(args);
        if (tasks == null) {
            System.err.println("Usage: MiniJava -S | -P | -A <file1.java, file2.java, ...>");
            System.exit(1);
        }

        int status = 0;
        while (!tasks.isEmpty()) {
            Task task = tasks.poll();
            switch (task.type) {
                case SCAN -> status |= scan(task);
                case PRETTY_PRINT -> status |= parse(task, new PrettyPrintVisitor());
                case AST -> status |= parse(task, new ASTVisitor());
            }
        }

        System.exit(status);
    }

    private static int scan(Task task) {
        int status = 0;
        try {
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
            Reader in = new BufferedReader(new InputStreamReader(new FileInputStream(task.input)));

            scanner s = new scanner(in, sf);
            Symbol t = s.next_token();

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
            ComplexSymbolFactory sf = new ComplexSymbolFactory();
            Reader in = new BufferedReader(new InputStreamReader(new FileInputStream(task.input)));

            scanner s = new scanner(in, sf);
            parser p = new parser(s, sf);
            Symbol root = p.parse();

            Program program = (Program) root.value;
            program.accept(visitor);
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
                default -> {
                    System.err.printf("Unrecognized operand: %s%n", operator);
                    return null;
                }
            }

            int j = i + 1;
            boolean foundValidArgs = false;
            while (j < args.length && args[j].charAt(0) != '-') {  // while not an operand
                String path = args[j];
                File f = new File(path);

                if (f.isDirectory()) {
                    // read all files within directory
                    File[] children = f.listFiles();
                    if (children == null) {
                        System.err.printf("Empty directory: %s%n", path);
                        return null;
                    }

                    for (File child : children) {
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
        SCAN, PRETTY_PRINT, AST
    }
}
