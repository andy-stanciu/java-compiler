package codegen.synth.def;

import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.Register.RDI;
import static codegen.platform.Register.RSI;

public class ConcatIntString extends ConcatStrings {
    public ConcatIntString(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.CONCAT_INT_STRING;
    }

    /**
     * Concatenates an int to the left of a string.
     * RDI: int
     * RSI: string pointer
     */
    @Override
    protected void generate() {
        concatInt(RSI, RDI);
        super.generate();
    }
}
