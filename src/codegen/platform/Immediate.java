package codegen.platform;

import codegen.platform.isa.ISA;

public record Immediate(int n) implements ISource {
    private static ISA isa;

    public static void setISA(ISA isa) {
        Immediate.isa = isa;
    }

    public static Immediate of(int n) {
        return new Immediate(n);
    }

    @Override
    public String toString() {
        return isa.toImmediate(this);
    }
}
