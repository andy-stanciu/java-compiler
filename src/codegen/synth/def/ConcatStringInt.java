package codegen.synth.def;

import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.Register.RDI;
import static codegen.platform.Register.RSI;

public class ConcatStringInt extends ConcatStrings {
    public ConcatStringInt(ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.CONCAT_STRING_INT;
    }

    /**
     * Concatenates an int to the right of a string.
     * RDI: string pointer
     * RSI: int
     */
    @Override
    protected void generate() {
        concatInt(RDI, RSI);
        super.generate();
    }
}
