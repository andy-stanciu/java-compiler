package codegen.platform;

import codegen.platform.isa.ISA;

public enum Register implements ISource, IDestination {
    RAX,
    RSP,
    RBP,
    RDI,
    RSI,
    RDX,
    RCX,
    R8,
    R9,
    R10,
    R11,
    RIP,
    CL;

    private static ISA isa;

    public static void setISA(ISA isa) {
        Register.isa = isa;
    }

    @Override
    public String toString() {
        return isa.getMapping(this);
    }
}
