package codegen.synth.def;

import codegen.Generator;
import codegen.platform.*;
import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.Operation.*;
import static codegen.platform.Register.*;
import static codegen.platform.Register.RAX;

public class ConcatStrings extends SyntheticFunctionDefinition {
    public ConcatStrings(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.CONCAT_STRING_STRING;
    }

    /**
     * Concatenates two strings.
     * RDI: pointer to first string
     * RSI: pointer to second string
     */
    @Override
    protected void generate() {
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
     * Helper function that concatenates a string and a bool.
     * @param strReg Register containing pointer to a string.
     * @param boolReg Register containing a bool.
     */
    protected void concatBool(Register strReg, Register boolReg) {
        var concatBoolFalseLabel = generator.nextLabel("concat_bool_false");
        var concatBoolDoneLabel = generator.nextLabel("concat_bool_done");
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
    }

    /**
     * Helper function that concatenates a string and an int.
     * @param strReg Register containing pointer to a string.
     * @param intReg Register containing an int.
     */
    protected void concatInt(Register strReg, Register intReg) {
        generator.genPush(strReg);
        generator.genBinary(MOV, intReg, RDI);
        generator.genCall(SyntheticFunction.LOAD_STRING_INT);
        generator.genPop(strReg);
        generator.genBinary(MOV, RAX, intReg);
    }
}
