package codegen.synth.def;

import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.Register.RDI;
import static codegen.platform.Register.RSI;

public class ConcatBoolString extends ConcatStrings {
    public ConcatBoolString(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.CONCAT_BOOL_STRING;
    }

    /**
     * Concatenates a boolean to the left of a string.
     * RDI: boolean
     * RSI: string pointer
     */
    @Override
    protected void generate() {
        concatBool(RSI, RDI);
        super.generate();
    }
}
