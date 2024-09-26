package codegen.platform;

import codegen.platform.isa.ISA;

public enum Directive {
    QUAD,
    GLOBAL;

    private static ISA isa;

    public static void setISA(ISA isa) {
        Directive.isa = isa;
    }

    @Override
    public String toString() {
        return isa.getMapping(this);
    }
}
