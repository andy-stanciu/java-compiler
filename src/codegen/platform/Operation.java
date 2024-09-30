package codegen.platform;

import codegen.platform.isa.ISA;

public enum Operation {
    MOV,
    ADD,
    SUB,
    IMUL,
    IDIV,
    LEA,
    PUSH,
    POP,
    RET,
    JMP,
    JE,
    JNE,
    JL,
    JLE,
    JG,
    JGE,
    SHL,
    SHR,
    SAR,
    NEG,
    AND,
    OR,
    XOR,
    NOT,
    CMP,
    TEST,
    CQTO,
    CALL;

    private static ISA isa;

    public static void setISA(ISA isa) {
        Operation.isa = isa;
    }

    @Override
    public String toString() {
        return isa.getMapping(this);
    }
}
