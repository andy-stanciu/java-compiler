package codegen.platform;

import codegen.platform.isa.ISA;

public record Label(String name) {
    private static ISA isa;

    public static void setISA(ISA isa) {
        Label.isa = isa;
    }

    public static Label of (String name) {
        return new Label(name);
    }

    @Override
    public String toString() {
        return isa.toLabel(this);
    }
}
