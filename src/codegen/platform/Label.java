package codegen.platform;

import codegen.platform.isa.ISA;

public record Label(String name, boolean literal) {
    private static ISA isa;

    public static void setISA(ISA isa) {
        Label.isa = isa;
    }

    public static Label of(String name) {
        return new Label(name, false);
    }

    public static Label of(String name, boolean literal) {
        return new Label(name, literal);
    }

    @Override
    public String toString() {
        if (literal) {
            return name;
        }
        return isa.toLabel(this);
    }
}
