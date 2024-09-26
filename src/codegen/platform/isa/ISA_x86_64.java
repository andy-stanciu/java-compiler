package codegen.platform.isa;

import codegen.platform.*;

public final class ISA_x86_64 extends ISA {
    @Override
    public String toUnaryInstruction(Directive dir, Label label) {
        return String.format("%s%s%s", dir, getTab(dir), label);
    }

    @Override
    public String toUnaryInstruction(Operation op, Label label) {
        return String.format("%s%s%s", op, getTab(op), label);
    }

    @Override
    public String toUnaryInstruction(Operation op, ISource src) {
        return String.format("%s%s%s", op, getTab(op), src);
    }

    @Override
    public String toBinaryInstruction(Operation op, ISource src, IDestination dst) {
        return String.format("%s%s%s,%s", op, getTab(op), src, dst);
    }

    @Override
    public String toImmediate(Immediate immediate) {
        return "$" + immediate.n();
    }

    @Override
    public String toLabel(Label label) {
        return label.name();
    }

    @Override
    public String toMemoryLocation(Memory memory) {
        return memory.offset() + "(" + memory.register() + ")";
    }

    @Override
    public String toMemoryScaledIndexLocation(MemoryScaledIndex memoryScaledIndex) {
        return memoryScaledIndex.offset() + "(" + memoryScaledIndex.base() + "," +
                memoryScaledIndex.index() + "," + memoryScaledIndex.scale() + ")";
    }

    @Override
    String mov() {
        return "movq";
    }

    @Override
    String add() {
        return "addq";
    }

    @Override
    String sub() {
        return "subq";
    }

    @Override
    String imul() {
        return "imulq";
    }

    @Override
    String idiv() {
        return "idivq";
    }

    @Override
    String lea() {
        return "leaq";
    }

    @Override
    String push() {
        return "pushq";
    }

    @Override
    String pop() {
        return "popq";
    }

    @Override
    String ret() {
        return "ret";
    }

    @Override
    String jmp() {
        return "jmp";
    }

    @Override
    String je() {
        return "je";
    }

    @Override
    String jne() {
        return "jne";
    }

    @Override
    String jl() {
        return "jl";
    }

    @Override
    String jle() {
        return "jle";
    }

    @Override
    String jg() {
        return "jg";
    }

    @Override
    String jge() {
        return "jge";
    }

    @Override
    String shl() {
        return "shlq";
    }

    @Override
    String shr() {
        return "shrq";
    }

    @Override
    String sar() {
        return "sarq";
    }

    @Override
    String neg() {
        return "negq";
    }

    @Override
    String and() {
        return "andq";
    }

    @Override
    String or() {
        return "orq";
    }

    @Override
    String xor() {
        return "xorq";
    }

    @Override
    String not() {
        return "notq";
    }

    @Override
    String cmp() {
        return "cmpq";
    }

    @Override
    String test() {
        return "testq";
    }

    @Override
    String cqto() {
        return "cqto";
    }

    @Override
    String call() {
        return "call";
    }

    @Override
    String quad() {
        return ".quad";
    }

    @Override
    String global() {
        return ".globl";
    }

    @Override
    String rax() {
        return "%rax";
    }

    @Override
    String rsp() {
        return "%rsp";
    }

    @Override
    String rbp() {
        return "%rbp";
    }

    @Override
    String rdi() {
        return "%rdi";
    }

    @Override
    String rsi() {
        return "%rsi";
    }

    @Override
    String rdx() {
        return "%rdx";
    }

    @Override
    String rcx() {
        return "%rcx";
    }

    @Override
    String r8() {
        return "%r8";
    }

    @Override
    String r9() {
        return "%r9";
    }

    @Override
    String r10() {
        return "%r10";
    }

    @Override
    String r11() {
        return "%r11";
    }

    @Override
    String rip() {
        return "%rip";
    }

    @Override
    String cl() {
        return "%cl";
    }
}
