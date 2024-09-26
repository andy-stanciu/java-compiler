package codegen.platform;

import codegen.platform.isa.ISA;

public record Memory(Register register, String offset) implements ISource, IDestination {
    private static ISA isa;

    public static void setISA(ISA isa) {
        Memory.isa = isa;
    }

    public Memory(Register register, int offset) {
        this(register, String.valueOf(offset));
    }

    public static Memory of(Register register, int offset) {
        return new Memory(register, offset);
    }

    public static Memory of(Register register, String offset) {
        return new Memory(register, offset);
    }

    @Override
    public String toString() {
        return isa.toMemoryLocation(this);
    }
}
