package codegen.synth.def;

import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.Register.RDI;
import static codegen.platform.Register.RSI;

public class ConcatNullString extends ConcatStrings {
    public ConcatNullString(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.CONCAT_NULL_STRING;
    }

    /**
     * Concatenates a null pointer to the left of a string.
     * RDI: null pointer
     * RSI: string pointer
     */
    @Override
    protected void generate() {
        concatNull(RSI, RDI);
        super.generate();
    }
}
