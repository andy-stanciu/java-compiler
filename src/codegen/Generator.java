package codegen;

import codegen.platform.*;
import codegen.platform.isa.ISA;

import java.util.HashMap;
import java.util.Map;

import static codegen.platform.Operation.*;
import static codegen.platform.Register.*;

public final class Generator {
    private static Generator instance;
    public static final int WORD_SIZE = 8;
    public static final Register[] ARGUMENT_REGISTERS = new Register[] { RDI, RSI, RDX, RCX, R8, R9 };
    private static final int OPERATOR_SIZE = 8;
    private static final int INDENT_SIZE = 4;
    private static final int INSTRUCTION_SIZE = 32;
    private static final boolean COMMENTS_ENABLED = true;
    private int stackSize;
    private FlowContext context;
    private boolean assignable;
    private final Map<String, Integer> labelCounts;
    private final ISA isa;

    public static Generator getInstance(ISA isa) {
        if (instance == null) {
            instance = new Generator(isa);
        }
        return instance;
    }

    private Generator(ISA isa) {
        this.isa = isa;
        this.labelCounts = new HashMap<>();
    }

    /**
     * <p>
     *     Generates a unary instruction given an {@link Operation operation} and {@link ISource source}.
     * </p>
     *
     * <p>
     *     Sample x86-64 unary instruction: <br/> <code>idivq %rcx</code>
     * </p>
     *
     * @param op The operation to use.
     * @param src The source location to use.
     */
    public void genUnary(Operation op, ISource src) {
        genUnary(op, src, "");
    }

    /**
     * <p>
     *     Generates a commented unary instruction given an {@link Operation operation} and {@link ISource source}.
     * </p>
     *
     * <p>
     *     Sample x86-64 commented unary instruction: <br/>
     *     <code>idivq %rcx <tab></tab> # signed divide %rdx:%rax by %rcx</code>
     * </p>
     *
     * @param op The operation to use.
     * @param src The source location to use.
     * @param comment The comment to append at the end of the instruction.
     */
    public void genUnary(Operation op, ISource src, String comment) {
        String instruction = String.format("%s%s%s", op, getTab(op), src);
        gen(instruction, comment);
    }

    /**
     * <p>
     *     Generates a unary instruction given an {@link Operation operation} and label.
     * </p>
     *
     * <p>
     *     Sample x86-64 unary instruction: <br/>
     *     <code>jmp done</code>
     * </p>
     *
     * @param op The operation to use.
     * @param label The label to use.
     */
    public void genUnary(Operation op, Label label) {
        genUnary(op, label, "");
    }

    /**
     * <p>
     *     Generates a commented unary instruction given an {@link Operation operation} and label.
     * </p>
     *
     * <p>
     *     Sample x86-64 unary instruction: <br/>
     *     <code>jmp done <tab></tab> # unconditional jump to the label "done"</code>
     * </p>
     *
     * @param op The operation to use.
     * @param label The label to use.
     */
    public void genUnary(Operation op, Label label, String comment) {
        String instruction = String.format("%s%s%s", op, getTab(op), label);
        gen(instruction, comment);
    }

    /**
     * <p>
     *     Generates a unary instruction given an {@link Directive directive} and label.
     * </p>
     *
     * <p>
     *     Sample x86-64 unary instruction: <br/>
     *     <code>.quad 0</code>
     * </p>
     *
     * @param dir The directive to use.
     * @param label The label to use.
     */
    public void genUnary(Directive dir, Label label) {
        genUnary(dir, label, "");
    }

    /**
     * <p>
     *     Generates a commented unary instruction given an {@link Directive directive} and label.
     * </p>
     *
     * <p>
     *     Sample x86-64 unary instruction: <br/>
     *     <code>.quad 0 <tab></tab> # quadword data block pointing to null</code>
     * </p>
     *
     * @param dir The directive to use.
     * @param label The label to use.
     */
    public void genUnary(Directive dir, Label label, String comment) {
        String instruction = String.format("%s%s%s", dir, getTab(dir), label);
        gen(instruction, comment);
    }

    /**
     * <p>
     *     Generates a binary instruction given an {@link Operation operation}, {@link ISource source},
     *     and {@link IDestination destination}.
     * </p>
     *
     * <p>
     *     Sample x86-64 binary instruction: <br/>
     *     <code>movq %rdi,%rax</code>
     * </p>
     *
     * @param op The operation to use.
     * @param src The source location to use.
     * @param dst The destination location to use.
     */
    public void genBinary(Operation op, ISource src, IDestination dst) {
        genBinary(op, src, dst, "");
    }

    /**
     * <p>
     *     Generates a commented binary instruction given an {@link Operation operation}, {@link ISource source},
     *     and {@link IDestination destination}.
     * </p>
     *
     * <p>
     *     Sample x86-64 binary instruction: <br/>
     *     <code>movq %rdi,%rax <tab></tab> # copy value at %rdi to %rax</code>
     * </p>
     *
     * @param op The operation to use.
     * @param src The source location to use.
     * @param dst The destination location to use.
     */
    public void genBinary(Operation op, ISource src, IDestination dst, String comment) {
        String instruction = String.format("%s%s%s,%s", op, getTab(op), src, dst);
        gen(instruction, comment);
    }

    /**
     * <p>
     *     Generates a call instruction to the specified label.
     * </p>
     *
     * <p>
     *     Sample x86-64 binary instruction: <br/>
     *     <code>call func</code>
     * </p>
     *
     * @param label The label to use.
     */
    public void genCall(Label label) {
        genCall(label, "");
    }

    /**
     * <p>
     *     Generates a call instruction to the specified synthetic function.
     * </p>
     *
     * @param syntheticFunction The synthetic function to call.
     */
    public void genCall(SyntheticFunction syntheticFunction) {
        genCall(SyntheticFunctionGenerator.getSyntheticFunctionLabel(syntheticFunction));
    }

    /**
     * <p>
     *     Generates a call instruction to the specified C function.
     * </p>
     *
     * @param cFunction The C function to call.
     */
    public void genCall(CFunction cFunction) {
        genCall(CFunctionMapper.map(cFunction));
    }

    /**
     * <p>
     *     Generates a commented call instruction to the specified label.
     * </p>
     *
     * <p>
     *     Sample x86-64 binary instruction: <br/>
     *     <code>call func <tab></tab> # calls the label "func"</code>
     * </p>
     *
     * @param label The label to use.
     */
    public void genCall(Label label, String comment) {
        boolean aligned = stackSize % 2 == 0;
        if (!aligned) genBinary(SUB, Immediate.of(WORD_SIZE), RSP);
        genUnary(CALL, label, comment);
        if (!aligned) genBinary(ADD, Immediate.of(WORD_SIZE), RSP);
    }

    /**
     * <p>
     *     Generates a push instruction given a {@link ISource source}.
     * </p>
     *
     * <p>
     *     Sample x86-64 push instruction: <br/>
     *     <code>pushq %rax</code>
     * </p>
     *
     * @param src The source location to use.
     */
    public void genPush(ISource src) {
        genPush(src, "");
    }

    /**
     * <p>
     *     Generates a commented push instruction given a {@link ISource source}.
     * </p>
     *
     * <p>
     *     Sample x86-64 push instruction: <br/>
     *     <code>pushq %rax <tab></tab> # push %rax onto the stack</code>
     * </p>
     *
     * @param src The source location to use.
     */
    public void genPush(ISource src, String comment) {
        genUnary(PUSH, src, comment);
        stackSize++;
    }

    /**
     * <p>
     *     Generates a pop instruction given a {@link IDestination destination}.
     * </p>
     *
     * <p>
     *     Sample x86-64 pop instruction: <br/>
     *     <code>popq %rax</code>
     * </p>
     *
     * @param dst The destination to pop into.
     */
    public void genPop(IDestination dst) {
        genPop(dst, false, "");
    }

    public void genPop(IDestination dst, boolean popAll) {
        genPop(dst, true, "");
    }

    /**
     * <p>
     *     Generates a commented pop instruction given a {@link IDestination destination}.
     * </p>
     *
     * <p>
     *     Sample x86-64 pop instruction: <br/>
     *     <code>popq %rax <tab></tab> # pop top of stack into %rax</code>
     * </p>
     *
     * @param dst The destination to use.
     */
    public void genPop(IDestination dst, boolean popAll, String comment) {
        genUnary(POP, dst, comment);

        if (stackSize == 0) {
            throw new IllegalStateException();
        }
        if (popAll) {
            stackSize = 1; // pop everything except base pointer
        } else {
            stackSize--;
        }
    }

    /**
     * Generates a label.
     * @param label The name of the label.
     */
    public void genLabel(Label label) {
        System.out.printf("%s:%n", label);
    }

    /**
     * Generates a return instruction.
     */
    public void genReturn() {
        indent();
        System.out.println(RET);

        if (stackSize > 0) {
            throw new IllegalStateException();
        }
    }

    /**
     * Generates a stack frame prologue.
     */
    public void genPrologue() {
        genPush(RBP);
        genBinary(MOV, RSP, RBP);
    }

    /**
     * Generates a stack frame epilogue.
     */
    public void genEpilogue() {
        genBinary(MOV, RBP, RSP);
        genPop(RBP);
        genReturn();
    }

    /**
     * Generates the ASM code section header.
     */
    public void genCodeSection() {
        indent();
        System.out.println(".text");
        indent();
        System.out.printf("%s%sasm_main%n", ".globl", getTab(".globl"));
    }

    /**
     * Generates the ASM data section header.
     */
    public void genDataSection() {
        indent();
        System.out.println(".data");
    }

    /**
     * Generates a new line.
     */
    public void newLine() {
        System.out.println();
    }

    /**
     * Gets the ith argument {@link Register register}.
     * @param i The index of the argument register to retrieve.
     */
    public Register getArgumentRegister(int i) {
        if (i < 0 || i >= ARGUMENT_REGISTERS.length) {
            throw new IllegalArgumentException();
        }

        return ARGUMENT_REGISTERS[i];
    }

    /**
     * Pushes all argument registers onto the stack.
     */
    public void pushArgumentRegisters() {
        for (int i = 0; i < ARGUMENT_REGISTERS.length; i++) {
            genPush(getArgumentRegister(i));
        }
    }

    /**
     * Pops all argument registers off the stack.
     */
    public void popArgumentRegisters() {
        for (int i = ARGUMENT_REGISTERS.length - 1; i >= 0 ; i--) {
            genPop(getArgumentRegister(i));
        }
    }

    /**
     * Clears all argument registers.
     */
    public void clearArgumentRegisters() {
        for (int i = 0; i < ARGUMENT_REGISTERS.length; i++) {
            genBinary(XOR, getArgumentRegister(i), getArgumentRegister(i));
        }
    }

    /**
     * "Left shifts" all argument registers, zeroing out the last argument register.
     */
    public void leftShiftArgumentRegisters() {
        for (int i = 1; i < ARGUMENT_REGISTERS.length; i++) {
            genBinary(MOV, getArgumentRegister(i), getArgumentRegister(i - 1));
        }
        genBinary(XOR, getArgumentRegister(ARGUMENT_REGISTERS.length - 1),
                getArgumentRegister(ARGUMENT_REGISTERS.length - 1));
    }

    /**
     * "Right shifts" all argument registers, zeroing out the first argument register.
     */
    public void rightShiftArgumentRegisters() {
        for (int i = ARGUMENT_REGISTERS.length - 2; i >= 0; i--) {
            genBinary(MOV, getArgumentRegister(i), getArgumentRegister(i + 1));
        }
        genBinary(XOR, getArgumentRegister(0), getArgumentRegister(0));
    }

    /**
     * Retrieves the next available label with the specified name.
     * @param name The prefix of the label.
     * @return The label name, concatenated with an auto-incremented id.
     */
    public Label nextLabel(String name) {
        int count = labelCounts.getOrDefault(name, 0);
        labelCounts.put(name, count + 1);

        return Label.of(name + count);
    }

    /**
     * Pushes the specified {@link FlowContext flow context}.
     */
    public void push(FlowContext context) {
        this.context = context;
    }

    /**
     * Pops the specified {@link FlowContext flow context}.
     */
    public FlowContext pop() {
        var ret = context;
        context = null;
        return ret;
    }

    /**
     * Signals that the expression in scope is assignable.
     */
    public void signalAssignable() {
        assignable = true;
    }

    /**
     * Verifies whether the expression in scope is assignable.
     */
    public boolean isAssignable() {
        var ret = assignable;
        assignable = false;
        return ret;
    }

    /**
     * Generates the specified instruction with the specified comment.
     * @param instruction The instruction to generate.
     * @param comment The comment to follow the instruction.
     */
    public void gen(String instruction, String comment) {
        indent();
        if (COMMENTS_ENABLED && !comment.isBlank()) {
            System.out.printf("%s%s# %s%n", instruction, getCommentTab(instruction), comment);
        } else {
            System.out.printf("%s%n", instruction);
        }
    }

    /**
     * Generates the specified instruction.
     * @param instruction The instruction to generate.
     */
    public void gen(String instruction) {
        gen(instruction, "");
    }

    /**
     * Generates the specified zero-argument {@link Operation operation}.
     * @param operation The operation to generate.
     */
    public void gen(Operation operation) {
        gen(String.valueOf(operation), "");
    }

    private void genUnary(Operation op, IDestination dst) {
        genUnary(op, dst, "");
    }

    private void genUnary(Operation op, IDestination dst, String comment) {
        String instruction = String.format("%s%s%s", op, getTab(op), dst);
        gen(instruction, comment);
    }

    private void indent() {
        System.out.print(" ".repeat(INDENT_SIZE));
    }

    private String getTab(Operation op) {
        return getTab(op.toString());
    }

    private String getTab(Directive dir) {
        return getTab(dir.toString());
    }

    private String getTab(String str) {
        return " ".repeat(OPERATOR_SIZE - str.length());
    }

    private String getCommentTab(String instruction) {
        return " ".repeat(INSTRUCTION_SIZE - instruction.length());
    }
}
