package codegen.synth.def;

import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.Register.RDI;
import static codegen.platform.Register.RSI;

public class ConcatStringBool extends ConcatStrings {
    public ConcatStringBool(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.CONCAT_STRING_BOOL;
    }

    /**
     * Concatenates a boolean to the right of a string.
     * RDI: string pointer
     * RSI: boolean
     */
    @Override
    protected void generate() {
        concatBool(RDI, RSI);
        super.generate();
    }
}
