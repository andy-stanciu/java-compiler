package codegen;

import codegen.platform.Immediate;
import codegen.platform.Memory;
import codegen.platform.MemoryScaledIndex;
import codegen.platform.isa.ISA;

import java.util.HashMap;
import java.util.Map;

import static codegen.platform.Operation.*;
import static codegen.platform.Register.*;

public final class SyntheticFunctionGenerator {
    private static final String SYNTHETIC_POSTFIX = "$$";
    private static final Map<SyntheticFunction, String> syntheticFunctionTable = new HashMap<>();
    private static SyntheticFunctionGenerator instance;
    private final Generator generator;

    public static SyntheticFunctionGenerator getInstance(ISA isa) {
        if (instance == null) {
            instance = new SyntheticFunctionGenerator(isa);
        }
        return instance;
    }

    public static String getSyntheticFunctionLabel(SyntheticFunction syntheticFunction) {
        if (!syntheticFunctionTable.containsKey(syntheticFunction)) {
            throw new IllegalArgumentException();
        }
        return syntheticFunctionTable.get(syntheticFunction);
    }

    static {
        syntheticFunctionTable.put(SyntheticFunction.ALLOCATE_ARRAY, "alloc_arr" + SYNTHETIC_POSTFIX);
        syntheticFunctionTable.put(SyntheticFunction.ALLOCATE_NESTED_ARRAY, "alloc_nested_arr" + SYNTHETIC_POSTFIX);
    }

    private SyntheticFunctionGenerator(ISA isa) {
        this.generator = Generator.getInstance(isa);
    }

    public void generateAll() {
        generateAllocateArray();
        generateAllocateNestedArray();
    }

    private void generateAllocateNestedArray() {
        String arrayDoneLabel = generator.nextLabel("arr_done");
        String arrayLoopLabel = generator.nextLabel("arr_loop");

        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.ALLOCATE_NESTED_ARRAY));
        generator.genPrologue();
        generator.genBinary(CMP, Immediate.of(0), RDI);
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
        generator.genCall("alloc_nested_arr" + SYNTHETIC_POSTFIX);
        generator.popArgumentRegisters();
        generator.genPop(R10);  // restore loop index
        generator.genPop(RAX);  // restore arr pointer in rax
        generator.genBinary(ADD, Immediate.of(1), R10);  // i++
        generator.genUnary(JMP, arrayLoopLabel);
        generator.genLabel(arrayDoneLabel);
        generator.genEpilogue();
    }

    private void generateAllocateArray() {
        generator.genLabel( "alloc_arr" + SYNTHETIC_POSTFIX);
        generator.genPrologue();

        generator.genPush(RDI);                               // push sizeof(arr) onto stack
        generator.genBinary(ADD, Immediate.of(1), RDI);       // arr[0] = sizeof(arr)
        generator.genBinary(SHL, Immediate.of(3), RDI);       // multiply by word size (8)
        generator.genCall("mjcalloc");                          // allocate space on heap
        generator.genPop(RDI);                                // pop sizeof(arr) into rdi
        generator.genBinary(MOV, RDI, Memory.of(RAX, 0));  // store sizeof(arr) at start of array

        generator.genEpilogue();
    }
}
