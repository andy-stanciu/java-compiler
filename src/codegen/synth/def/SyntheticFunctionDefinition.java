package codegen.synth.def;

import codegen.Generator;
import codegen.platform.Label;
import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;
import lombok.Getter;
import lombok.Setter;

public abstract class SyntheticFunctionDefinition {
    private static final String SYNTHETIC_POSTFIX = "$$";
    protected final Generator generator;

    @Getter
    @Setter
    private boolean referenced;

    public SyntheticFunctionDefinition(final ISA isa) {
        this.generator = Generator.getInstance(isa);
    }

    public final void generateFunction() {
        generator.genLabel(getLabel());
        generator.genPrologue();
        generate();
        generator.genEpilogue();
    }

    public final Label getLabel() {
        return Label.of(getName().getLabel() + SYNTHETIC_POSTFIX);
    }

    public abstract SyntheticFunction getName();
    protected abstract void generate();
}
