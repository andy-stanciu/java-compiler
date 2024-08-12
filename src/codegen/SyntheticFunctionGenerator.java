package codegen;

import java.util.HashMap;
import java.util.Map;

public final class SyntheticFunctionGenerator {
    private static final String SYNTHETIC_PREFIX = "$$";
    private static final Map<SyntheticFunction, String> syntheticFunctionTable = new HashMap<>();
    private static final SyntheticFunctionGenerator instance = new SyntheticFunctionGenerator();
    private final Generator generator;

    public static SyntheticFunctionGenerator getInstance() {
        return instance;
    }

    public static String getSyntheticFunctionLabel(SyntheticFunction syntheticFunction) {
        if (!syntheticFunctionTable.containsKey(syntheticFunction)) {
            throw new IllegalArgumentException();
        }
        return syntheticFunctionTable.get(syntheticFunction);
    }

    static {
        syntheticFunctionTable.put(SyntheticFunction.ALLOCATE_ARRAY, SYNTHETIC_PREFIX + "alloc_arr");
        syntheticFunctionTable.put(SyntheticFunction.ALLOCATE_NESTED_ARRAY, SYNTHETIC_PREFIX + "alloc_nested_arr");
    }

    private SyntheticFunctionGenerator() {
        this.generator = Generator.getInstance();
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
        generator.genBinary("cmpq", "$0", "%rdi");
        generator.genUnary("je", arrayDoneLabel);
        generator.genBinary("movq", "$1", "%r10");
        generator.genLabel(arrayLoopLabel);
        generator.genBinary("cmpq", "%r10", "%rdi");
        generator.genUnary("jl", arrayDoneLabel);
        generator.genPush("%rax");  // save arr pointer
        generator.genPush("%r10");  // save loop index (arr[i])
        generator.pushArgumentRegisters();
        generator.genBinary("movq", "%rsi", "%rdi");
        generator.genCall(SyntheticFunction.ALLOCATE_ARRAY);  // alloc nested arr, result in rax
        generator.popArgumentRegisters();
        generator.genPop("%r10");  // restore loop index in r10
        generator.genPop("%r11");  // restore arr pointer in r11
        generator.genBinary("movq", "%rax", "(%r11,%r10," + Generator.WORD_SIZE + ")");  // arr[i] = nested arr
        generator.genPush("%r11");  // save arr pointer
        generator.genPush("%r10");  // save loop index
        generator.pushArgumentRegisters();
        generator.leftShiftArgumentRegisters();
        generator.genCall(SYNTHETIC_PREFIX + "alloc_nested_arr");
        generator.popArgumentRegisters();
        generator.genPop("%r10");  // restore loop index
        generator.genPop("%rax");  // restore arr pointer in rax
        generator.genBinary("addq", "$1", "%r10");  // i++
        generator.genUnary("jmp", arrayLoopLabel);
        generator.genLabel(arrayDoneLabel);
        generator.genEpilogue();
    }

    private void generateAllocateArray() {
        generator.genLabel(SYNTHETIC_PREFIX + "alloc_arr");
        generator.genPrologue();

        generator.genPush("%rdi");                               // push sizeof(arr) onto stack
        generator.genBinary("addq", "$1", "%rdi");       // arr[0] = sizeof(arr)
        generator.genBinary("shlq", "$3", "%rdi");       // multiply by word size (8)
        generator.genCall("mjcalloc");                          // allocate space on heap
        generator.genPop("%rdi");                                // pop sizeof(arr) into rdi
        generator.genBinary("movq", "%rdi", "0(%rax)");  // store sizeof(arr) at start of array

        generator.genEpilogue();
    }
}
