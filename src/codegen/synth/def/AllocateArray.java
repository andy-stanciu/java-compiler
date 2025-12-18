package codegen.synth.def;

import codegen.platform.Immediate;
import codegen.platform.Memory;
import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.CFunction.MALLOC;
import static codegen.platform.Operation.*;
import static codegen.platform.Register.RAX;
import static codegen.platform.Register.RDI;

public class AllocateArray extends SyntheticFunctionDefinition {
    public AllocateArray(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.ALLOCATE_ARRAY;
    }

    /**
     * Allocates a new 1-dimensional array.
     * RDI: sizeof(arr)
     */
    @Override
    protected void generate() {
        generator.genPush(RDI);  // push sizeof(arr) onto stack
        generator.genBinary(ADD, Immediate.of(1), RDI);  // arr[0] = sizeof(arr)
        generator.genBinary(SHL, Immediate.of(3), RDI);  // multiply by word size (8)
        generator.genCall(MALLOC);  // allocate space on heap
        generator.genPop(RDI);  // pop sizeof(arr) into rdi
        generator.genBinary(MOV, RDI, Memory.of(RAX, 0));  // store sizeof(arr) at start of array
    }
}
