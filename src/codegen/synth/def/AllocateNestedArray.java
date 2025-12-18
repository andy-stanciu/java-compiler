package codegen.synth.def;

import codegen.Generator;
import codegen.platform.Immediate;
import codegen.platform.MemoryScaledIndex;
import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.Operation.*;
import static codegen.platform.Operation.JMP;
import static codegen.platform.Register.*;
import static codegen.platform.Register.R10;

public class AllocateNestedArray extends SyntheticFunctionDefinition {
    public AllocateNestedArray(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.ALLOCATE_NESTED_ARRAY;
    }

    @Override
    protected void generate() {
        var arrayDoneLabel = generator.nextLabel("arr_done");
        var arrayLoopLabel = generator.nextLabel("arr_loop");

        generator.genBinary(CMP, Immediate.of(0), RSI);  // if nested arr size is zero, we're done
        generator.genUnary(JE, arrayDoneLabel);
        generator.genBinary(MOV, Immediate.of(1), R10);
        generator.genLabel(arrayLoopLabel);
        generator.genBinary(CMP, R10, RDI);
        generator.genUnary(JL, arrayDoneLabel);
        generator.genPush(RAX);  // save arr pointer
        generator.genPush(R10);  // save loop index (arr[i])
        generator.pushArgumentRegisters();
        generator.genBinary(MOV, RSI, RDI);
        generator.genCall(SyntheticFunction.ALLOCATE_ARRAY);  // alloc nested arr, result in rax
        generator.popArgumentRegisters();
        generator.genPop(R10);  // restore loop index in r10
        generator.genPop(R11);  // restore arr pointer in r11
        generator.genBinary(MOV, RAX, MemoryScaledIndex.of(R11, R10, Generator.WORD_SIZE, 0));  // arr[i] = nested arr
        generator.genPush(R11);  // save arr pointer
        generator.genPush(R10);  // save loop index
        generator.pushArgumentRegisters();
        generator.leftShiftArgumentRegisters();
        generator.genCall(SyntheticFunction.ALLOCATE_NESTED_ARRAY);
        generator.popArgumentRegisters();
        generator.genPop(R10);  // restore loop index
        generator.genPop(RAX);  // restore arr pointer in rax
        generator.genBinary(ADD, Immediate.of(1), R10);  // i++
        generator.genUnary(JMP, arrayLoopLabel);
        generator.genLabel(arrayDoneLabel);
    }
}
