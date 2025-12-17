package codegen;

import codegen.platform.*;
import codegen.platform.isa.ISA;

import java.util.HashMap;
import java.util.Map;

import static codegen.platform.CFunction.MALLOC;
import static codegen.platform.CFunction.PRINT;
import static codegen.platform.Operation.*;
import static codegen.platform.Register.*;

public final class SyntheticFunctionGenerator {
    private static final String SYNTHETIC_POSTFIX = "$$";
    private static final Map<SyntheticFunction, Label> syntheticFunctionTable = new HashMap<>();
    private static SyntheticFunctionGenerator instance;
    private final Generator generator;

    public static SyntheticFunctionGenerator getInstance(ISA isa) {
        if (instance == null) {
            instance = new SyntheticFunctionGenerator(isa);
        }
        return instance;
    }

    public static Label getSyntheticFunctionLabel(SyntheticFunction syntheticFunction) {
        if (!syntheticFunctionTable.containsKey(syntheticFunction)) {
            throw new IllegalArgumentException();
        }
        return syntheticFunctionTable.get(syntheticFunction);
    }

    static {
        syntheticFunctionTable.put(SyntheticFunction.ALLOCATE_ARRAY, Label.of("alloc_arr" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.ALLOCATE_NESTED_ARRAY, Label.of("alloc_nested_arr" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.CONCAT_STRING_STRING, Label.of("concat_string_string" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.CONCAT_STRING_BOOL, Label.of("concat_string_bool" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.CONCAT_STRING_INT, Label.of("concat_string_int" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.CONCAT_INT_STRING, Label.of("concat_int_string" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.CONCAT_BOOL_STRING, Label.of("concat_bool_string" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.LOAD_STRING_TRUE, Label.of("load_string_true" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.LOAD_STRING_FALSE, Label.of("load_string_false" + SYNTHETIC_POSTFIX));
        syntheticFunctionTable.put(SyntheticFunction.LOAD_STRING_INT, Label.of("load_string_int" + SYNTHETIC_POSTFIX));
    }

    private SyntheticFunctionGenerator(ISA isa) {
        this.generator = Generator.getInstance(isa);
    }

    public void generateAll() {
        // TODO: make these only generate when necessary
        generateAllocateArray();
        generateAllocateNestedArray();
        generateConcatStringString();
        generateConcatStringBool();
        generateConcatBoolString();
        generateConcatStringInt();
        generateConcatIntString();
        generateLoadStringTrue();
        generateLoadStringFalse();
        generateLoadStringInt();
    }

    /**
     * Concatenates two strings.
     */
    private void generateConcatStringString() {
        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.CONCAT_STRING_STRING));
        generator.genPrologue();
        generateConcat();
        generator.genEpilogue();
    }

    /**
     * Concatenates a boolean to the right of a string.
     */
    private void generateConcatStringBool() {
        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.CONCAT_STRING_BOOL));
        concatBool(RDI, RSI);
    }

    /**
     * Concatenates a boolean to the left of a string.
     */
    private void generateConcatBoolString() {
        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.CONCAT_BOOL_STRING));
        concatBool(RSI, RDI);
    }

    /**
     * Concatenates an int to the right of a string.
     */
    private void generateConcatStringInt() {
        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.CONCAT_STRING_INT));
        concatInt(RDI, RSI);
    }

    /**
     * Concatenates an int to the left of a string.
     */
    private void generateConcatIntString() {
        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.CONCAT_INT_STRING));
        concatInt(RSI, RDI);
    }

    /**
     * Helper function that concatenates a string and a bool.
     * @param strReg Register containing pointer to a string.
     * @param boolReg Register containing a bool.
     */
    private void concatBool(Register strReg, Register boolReg) {
        var concatBoolFalseLabel = generator.nextLabel("concat_bool_false");
        var concatBoolDoneLabel = generator.nextLabel("concat_bool_done");
        generator.genPrologue();
        generator.genPush(strReg);
        generator.genBinary(CMP, Immediate.of(0), boolReg);
        generator.genUnary(JE, concatBoolFalseLabel);
        generator.genCall(SyntheticFunction.LOAD_STRING_TRUE);
        generator.genUnary(JMP, concatBoolDoneLabel);
        generator.genLabel(concatBoolFalseLabel);
        generator.genCall(SyntheticFunction.LOAD_STRING_FALSE);
        generator.genLabel(concatBoolDoneLabel);
        generator.genPop(strReg);
        generator.genBinary(MOV, RAX, boolReg);
        generateConcat();
        generator.genEpilogue();
    }

    /**
     * Helper function that concatenates a string and an int.
     * @param strReg Register containing pointer to a string.
     * @param intReg Register containing an int.
     */
    private void concatInt(Register strReg, Register intReg) {
        generator.genPrologue();
        generator.genPush(strReg);
        generator.genBinary(MOV, intReg, RDI);
        generator.genCall(SyntheticFunction.LOAD_STRING_INT);
        generator.genPop(strReg);
        generator.genBinary(MOV, RAX, intReg);
        generateConcat();
        generator.genEpilogue();
    }

    /**
     * Helper function that concatenates two strings located in rsi and rdi. Leaves the result in rax.
     */
    private void generateConcat() {
        generator.genBinary(MOV, Memory.of(RSI, 0), RCX);
        generator.genBinary(ADD, Memory.of(RDI, 0), RCX);  // sum of str lengths in rcx
        generator.genPush(RSI);  // rhs str ptr
        generator.genPush(RDI);  // lhs str ptr
        generator.genBinary(MOV, RCX, RDI);
        generator.genBinary(ADD, Immediate.of(1), RDI);
        generator.genBinary(SHL, Immediate.of(3), RDI);  // multiply by 8 to calc # of bytes
        generator.genPush(RCX);  // len(str1) + len(str2)
        generator.genCall(CFunction.MALLOC);  // malloc new string
        generator.genPop(RCX);  // len(str1) + len(str2)
        generator.genPop(RSI);  // lhs str ptr
        generator.genBinary(MOV, RCX, Memory.of(RAX, 0)); // put len(str1) + len(str2) at the start
        // load ptr to start of new string in rdi, excluding the size
        generator.genBinary(LEA, Memory.of(RAX, Generator.WORD_SIZE), RDI);
        generator.genBinary(MOV, Memory.of(RSI, 0), RDX); // load len(str1) in rdx
        generator.genBinary(SHL, Immediate.of(3), RDX);
        generator.genBinary(LEA, Memory.of(RSI, Generator.WORD_SIZE), RSI);  // rsi++
        generator.genPush(RAX);  // save new string ptr
        generator.genPush(RDX);  // save len(str1)
        generator.genCall(CFunction.MEMCOPY);
        generator.genPop(RDX);  // restore len(str1)
        generator.genPop(R8);   // restore new string ptr
        generator.genPop(RSI);  // rhs str ptr
        // load ptr to start of concatenation
        generator.genBinary(LEA, MemoryScaledIndex.of(R8, RDX, 1, Generator.WORD_SIZE), RDI);
        generator.genBinary(MOV, Memory.of(RSI, 0), RDX); // load len(str2) in rdx
        generator.genBinary(SHL, Immediate.of(3), RDX);
        generator.genBinary(LEA, Memory.of(RSI, Generator.WORD_SIZE), RSI);  // rsi++
        generator.genPush(R8);  // save new string ptr
        generator.genCall(CFunction.MEMCOPY);
        generator.genPop(RAX);  // pop new string ptr into rax
    }

    /**
     * Loads the string literal "true" into rax.
     */
    private void generateLoadStringTrue() {
        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.LOAD_STRING_TRUE));
        generator.genPrologue();
        generator.genBinary(MOV, Immediate.of(5 * Generator.WORD_SIZE), RDI); // sizeof(true) + 1
        generator.genCall(MALLOC);
        generator.genBinary(MOV, Immediate.of(4), Memory.of(RAX, 0)); // mark size as 4
        generator.genBinary(MOV, Immediate.of('t'), Memory.of(RAX, Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('r'), Memory.of(RAX, 2 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('u'), Memory.of(RAX, 3 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('e'), Memory.of(RAX, 4 * Generator.WORD_SIZE));
        generator.genEpilogue();
    }

    /**
     * Loads the string literal "false" into rax.
     */
    private void generateLoadStringFalse() {
        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.LOAD_STRING_FALSE));
        generator.genPrologue();
        generator.genBinary(MOV, Immediate.of(6 * Generator.WORD_SIZE), RDI); // sizeof(false) + 1
        generator.genCall(MALLOC);
        generator.genBinary(MOV, Immediate.of(5), Memory.of(RAX, 0)); // mark size as 5
        generator.genBinary(MOV, Immediate.of('f'), Memory.of(RAX, Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('a'), Memory.of(RAX, 2 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('l'), Memory.of(RAX, 3 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('s'), Memory.of(RAX, 4 * Generator.WORD_SIZE));
        generator.genBinary(MOV, Immediate.of('e'), Memory.of(RAX, 5 * Generator.WORD_SIZE));
        generator.genEpilogue();
    }

    /**
     * Loads a string literal from an integer into rax.
     */
    private void generateLoadStringInt() {
        var loadStringIntLabel = generator.nextLabel("load_string_int");
        var negationMarkedLabel = generator.nextLabel("negation_marked");
        var positiveIntLabel = generator.nextLabel("positive_int");
        var intTopLabel = generator.nextLabel("int_top");

        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.LOAD_STRING_INT));
        generator.genPrologue();

        // mark negative sign
        generator.genBinary(MOV, Immediate.of(0), R9); // mark as positive
        generator.genBinary(CMP, Immediate.of(0), RDI); // is the number negative?
        generator.genUnary(JGE, negationMarkedLabel);
        generator.genBinary(MOV, Immediate.of(1), R9); // mark as negative
        generator.genUnary(NEG, RDI); // make the number positive
        generator.genLabel(negationMarkedLabel);

        // push all digits onto the stack
        generator.genBinary(MOV, Immediate.of(0), RCX); // len = 0
        generator.genBinary(MOV, Immediate.of(10), R8); // divisor in r8
        generator.genBinary(MOV, RDI, RAX); // dividend in rax
        generator.genLabel(loadStringIntLabel);
        generator.gen(CQTO); // sign extend rax to rdx:rax
        generator.genUnary(IDIV, R8); // divide rdx:rax by r8, result in rax
        generator.genBinary(ADD, Immediate.of('0'), RDX); // cast to char
        generator.genPush(RDX); // push last digit char onto stack
        generator.genBinary(ADD, Immediate.of(1), RCX); // len++
        generator.genBinary(CMP, Immediate.of(0), RAX); // is rax 0?
        generator.genUnary(JNE, loadStringIntLabel); // no => jump to top

        // push '-' and increment len if negative
        generator.genBinary(CMP, Immediate.of(0), R9);
        generator.genUnary(JE, positiveIntLabel);
        generator.genPush(Immediate.of('-'));
        generator.genBinary(ADD, Immediate.of(1), RCX); // len++
        generator.genLabel(positiveIntLabel);

        // allocate string
        generator.genBinary(MOV, RCX, RDI); // copy len to rdi
        generator.genBinary(ADD, Immediate.of(1), RDI); // rdi++
        generator.genBinary(SHL, Immediate.of(3), RDI); // rdi *= 8
        generator.genPush(RCX); // len
        generator.genCall(MALLOC);
        generator.genPop(RCX);
        generator.genBinary(MOV, RCX, Memory.of(RAX, 0)); // load len
        generator.genBinary(MOV, Immediate.of(0), RDX); // idx
        generator.genLabel(intTopLabel);
        generator.genBinary(ADD, Immediate.of(1), RDX); // idx++
        generator.genPop(MemoryScaledIndex.of(RAX, RDX, Generator.WORD_SIZE, 0), true);
        generator.genBinary(CMP, RDX, RCX);
        generator.genUnary(JG, intTopLabel);

        generator.genEpilogue();
    }

    private void generateAllocateNestedArray() {
        var arrayDoneLabel = generator.nextLabel("arr_done");
        var arrayLoopLabel = generator.nextLabel("arr_loop");

        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.ALLOCATE_NESTED_ARRAY));
        generator.genPrologue();
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
        generator.genEpilogue();
    }

    private void generateAllocateArray() {
        generator.genLabel(getSyntheticFunctionLabel(SyntheticFunction.ALLOCATE_ARRAY));
        generator.genPrologue();

        generator.genPush(RDI);  // push sizeof(arr) onto stack
        generator.genBinary(ADD, Immediate.of(1), RDI);  // arr[0] = sizeof(arr)
        generator.genBinary(SHL, Immediate.of(3), RDI);  // multiply by word size (8)
        generator.genCall(MALLOC);  // allocate space on heap
        generator.genPop(RDI);  // pop sizeof(arr) into rdi
        generator.genBinary(MOV, RDI, Memory.of(RAX, 0));  // store sizeof(arr) at start of array

        generator.genEpilogue();
    }
}
