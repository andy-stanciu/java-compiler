package codegen.synth.def;

import codegen.Generator;
import codegen.platform.Immediate;
import codegen.platform.Memory;
import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.CFunction.MALLOC;
import static codegen.platform.Operation.MOV;
import static codegen.platform.Register.RAX;
import static codegen.platform.Register.RDI;

public class LoadStringTrue extends SyntheticFunctionDefinition {
    public LoadStringTrue(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.LOAD_STRING_TRUE;
    }

    /**
     * Loads the string literal "true" into rax.
     */
    @Override
    protected void generate() {
        generator.genBinary(MOV, Immediate.of(5 * Generator.WORD_SIZE), RDI); // sizeof(true) + 1
        generator.genCall(MALLOC);
        generator.genBinary(MOV, Immediate.of(4), Memory.of(RAX, 0)); // mark size as 4
        generator.genBinary(MOV, Immediate.of('t'), Memory.of(RAX, Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('r'), Memory.of(RAX, 2 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('u'), Memory.of(RAX, 3 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('e'), Memory.of(RAX, 4 * Generator.WORD_SIZE));
    }
}
