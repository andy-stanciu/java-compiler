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

public class LoadStringFalse extends SyntheticFunctionDefinition {
    public LoadStringFalse(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.LOAD_STRING_FALSE;
    }

    /**
     * Loads the string literal "false" into rax.
     */
    @Override
    protected void generate() {
        generator.genBinary(MOV, Immediate.of(6 * Generator.WORD_SIZE), RDI); // sizeof(false) + 1
        generator.genCall(MALLOC);
        generator.genBinary(MOV, Immediate.of(5), Memory.of(RAX, 0)); // mark size as 5
        generator.genBinary(MOV, Immediate.of('f'), Memory.of(RAX, Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('a'), Memory.of(RAX, 2 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('l'), Memory.of(RAX, 3 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('s'), Memory.of(RAX, 4 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('e'), Memory.of(RAX, 5 * Generator.WORD_SIZE));
    }
}
