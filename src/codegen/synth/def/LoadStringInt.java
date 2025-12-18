package codegen.synth.def;

import codegen.Generator;
import codegen.platform.Immediate;
import codegen.platform.Memory;
import codegen.platform.MemoryScaledIndex;
import codegen.platform.isa.ISA;
import codegen.synth.SyntheticFunction;

import static codegen.platform.CFunction.MALLOC;
import static codegen.platform.Operation.*;
import static codegen.platform.Register.*;

public class LoadStringInt extends SyntheticFunctionDefinition {
    public LoadStringInt(final ISA isa) {
        super(isa);
    }

    @Override
    public SyntheticFunction getName() {
        return SyntheticFunction.LOAD_STRING_INT;
    }

    /**
     * Loads a string literal from an integer into rax.
     */
    @Override
    protected void generate() {
        var loadStringIntLabel = generator.nextLabel("load_string_int");
        var negationMarkedLabel = generator.nextLabel("negation_marked");
        var positiveIntLabel = generator.nextLabel("positive_int");
        var intTopLabel = generator.nextLabel("int_top");

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
    }
}
