package codegen.platform;

import codegen.platform.isa.ISA;

public record MemoryScaledIndex(Register base, Register index, int scale, int offset)
        implements ISource, IDestination {
    private static ISA isa;

    public static void setISA(ISA isa) {
        MemoryScaledIndex.isa = isa;
    }

    public static MemoryScaledIndex of(Register base, Register index, int scale, int offset) {
        return new MemoryScaledIndex(base, index, scale, offset);
    }

    @Override
    public String toString() {
        return isa.toMemoryScaledIndexLocation(this);
    }
}
