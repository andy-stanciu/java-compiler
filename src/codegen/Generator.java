package codegen;

import java.util.HashMap;
import java.util.Map;

public final class Generator {
    private static final Generator instance = new Generator();
    public static final int WORD_SIZE = 8;
    public static final String[] ARGUMENT_REGISTERS = new String[] { "%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9" };
    private static final int OPERATOR_SIZE = 8;
    private static final int INDENT_SIZE = 4;
    private static final int INSTRUCTION_SIZE = 32;
    private static final boolean COMMENTS_ENABLED = true;
    private int stackSize;
    private FlowContext context;
    private boolean assignable;
    private final Map<String, Integer> labelCounts;

    public static Generator getInstance() {
        return instance;
    }

    private Generator() {
        this.labelCounts = new HashMap<>();
    }

    public void genUnary(String op, String arg) {
        genUnary(op, arg, "");
    }

    public void genUnary(String op, String arg, String comment) {
        String instruction = String.format("%s%s%s", op, getTab(op), arg);
        gen(instruction, comment);
    }

    public void genBinary(String op, String src, String dst) {
        genBinary(op, src, dst, "");
    }

    public void genBinary(String op, String src, String dst, String comment) {
        String instruction = String.format("%s%s%s,%s", op, getTab(op), src, dst);
        gen(instruction, comment);
    }

    public void genCall(String label) {
        genCall(label, "");
    }

    public void genCall(String label, String comment) {
        boolean aligned = stackSize % 2 == 0;
        if (!aligned) genBinary("subq", "$" + WORD_SIZE, "%rsp");
        genUnary("call", label, comment);
        if (!aligned) genBinary("addq", "$" + WORD_SIZE, "%rsp");
    }

    public void genPush(String arg) {
        genPush(arg, "");
    }

    public void genPush(String arg, String comment) {
        genUnary("pushq", arg, comment);
        stackSize++;
    }

    public void genPop(String arg) {
        genPop(arg, "");
    }

    public void genPop(String arg, String comment) {
        genUnary("popq", arg, comment);

        if (stackSize == 0) {
            throw new IllegalStateException();
        }
        stackSize--;
    }

    public void genLabel(String name) {
        System.out.printf("%s:%n", name);
    }

    public void genReturn() {
        indent();
        System.out.println("ret");

        if (stackSize > 0) {
            throw new IllegalStateException();
        }
    }

    public void genPrologue() {
        genUnary("pushq", "%rbp");
        genBinary("movq", "%rsp", "%rbp");
    }

    public void genEpilogue() {
        genBinary("movq", "%rbp", "%rsp");
        genUnary("popq", "%rbp");
        genReturn();
    }

    public void genCodeSection() {
        indent();
        System.out.println(".text");
        indent();
        System.out.printf("%s%sasm_main%n", ".globl", getTab(".globl"));
    }

    public void genDataSection() {
        indent();
        System.out.println(".data");
    }

    public void newLine() {
        System.out.println();
    }

    public String getArgumentRegister(int i) {
        if (i < 0 || i >= ARGUMENT_REGISTERS.length) {
            throw new IllegalArgumentException();
        }

        return ARGUMENT_REGISTERS[i];
    }

    public String nextLabel(String name) {
        int count = labelCounts.getOrDefault(name, 0);
        labelCounts.put(name, count + 1);

        return name + count;
    }

    public void push(FlowContext context) {
        this.context = context;
    }

    public FlowContext pop() {
        var ret = context;
        context = null;
        return ret;
    }

    public void signalAssignable() {
        assignable = true;
    }

    public boolean isAssignable() {
        var ret = assignable;
        assignable = false;
        return ret;
    }

    public void gen(String instruction, String comment) {
        indent();
        if (COMMENTS_ENABLED && !comment.isBlank()) {
            System.out.printf("%s%s# %s%n", instruction, getCommentTab(instruction), comment);
        } else {
            System.out.printf("%s%n", instruction);
        }
    }

    public void gen(String instruction) {
        gen(instruction, "");
    }

    private void indent() {
        System.out.print(" ".repeat(INDENT_SIZE));
    }

    private String getTab(String op) {
        return " ".repeat(OPERATOR_SIZE - op.length());
    }

    private String getCommentTab(String instruction) {
        return " ".repeat(INSTRUCTION_SIZE - instruction.length());
    }
}
