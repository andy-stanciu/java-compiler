package codegen.synth.def;

import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.Register.RDI;
import static codegen.platform.Register.RSI;

public class ConcatStringNull extends ConcatStrings {
    public ConcatStringNull(ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.CONCAT_STRING_NULL;
    }

    /**
     * Concatenates a null pointer to the right of a string.
     * RDI: string pointer
     * RSI: null pointer
     */
    @Override
    protected void generate() {
        concatNull(RDI, RSI);
        super.generate();
    }
}
