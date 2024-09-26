package codegen.platform.isa;

public final class ISAProvider {
    public static ISA getISA_x86_64() {
        return new ISA_x86_64();
    }
}
