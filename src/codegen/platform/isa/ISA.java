package codegen.platform.isa;

import codegen.platform.*;

import java.util.HashMap;
import java.util.Map;

import static codegen.platform.Directive.*;
import static codegen.platform.Operation.*;
import static codegen.platform.Register.*;

public abstract class ISA {
    private static final Map<Operation, String> operations = new HashMap<>();
    private static final Map<Directive, String> directives = new HashMap<>();
    private static final Map<Register, String> registers = new HashMap<>();

    public ISA() {
        // operations
        operations.put(MOV, mov());
        operations.put(ADD, add());
        operations.put(SUB, sub());
        operations.put(IMUL, imul());
        operations.put(IDIV, idiv());
        operations.put(LEA, lea());
        operations.put(PUSH, push());
        operations.put(POP, pop());
        operations.put(RET, ret());
        operations.put(JMP, jmp());
        operations.put(JE, je());
        operations.put(JNE, jne());
        operations.put(JL, jl());
        operations.put(JLE, jle());
        operations.put(JG, jg());
        operations.put(JGE, jge());
        operations.put(SHL, shl());
        operations.put(SHR, shr());
        operations.put(SAR, sar());
        operations.put(NEG, neg());
        operations.put(AND, and());
        operations.put(OR, or());
        operations.put(XOR, xor());
        operations.put(NOT, not());
        operations.put(CMP, cmp());
        operations.put(TEST, test());
        operations.put(CQTO, cqto());
        operations.put(CALL, call());

        // directives
        directives.put(QUAD, quad());

        // registers
        registers.put(RAX, rax());
        registers.put(RSP, rsp());
        registers.put(RBP, rbp());
        registers.put(RDI, rdi());
        registers.put(RSI, rsi());
        registers.put(RDX, rdx());
        registers.put(RCX, rcx());
        registers.put(R8, r8());
        registers.put(R9, r9());
        registers.put(R10, r10());
        registers.put(R11, r11());
        registers.put(RIP, rip());
        registers.put(CL, cl());

        // reflect ISA changes
        Operation.setISA(this);
        Directive.setISA(this);
        Register.setISA(this);
        Immediate.setISA(this);
        Memory.setISA(this);
        MemoryScaledIndex.setISA(this);
    }

    /**
     * @return The mapping for the specified {@link Operation}.
     */
    public String getMapping(Operation operation) {
        return operations.get(operation);
    }

    /**
     * @return The mapping for the specified {@link Directive}.
     */
    public String getMapping(Directive directive) {
        return directives.get(directive);
    }

    /**
     * @return The mapping for the specified {@link Register}.
     */
    public String getMapping(Register register) {
        return registers.get(register);
    }

    /**
     * Converts the specified immediate.
     * @param immediate The immediate to convert.
     * @return The converted immediate.
     */
    public abstract String toImmediate(Immediate immediate);

    /**
     * Converts the specified memory location.
     * @param memory The memory location to convert.
     * @return The converted memory location.
     */
    public abstract String toMemoryLocation(Memory memory);

    /**
     * Converts the specified memory scaled index location.
     * @param memoryScaledIndex The memory scaled index location to convert.
     * @return The converted memory scaled index location.
     */
    public abstract String toMemoryScaledIndexLocation(MemoryScaledIndex memoryScaledIndex);

    // operations
    abstract String mov();
    abstract String add();
    abstract String sub();
    abstract String imul();
    abstract String idiv();
    abstract String lea();
    abstract String push();
    abstract String pop();
    abstract String ret();
    abstract String jmp();
    abstract String je();
    abstract String jne();
    abstract String jl();
    abstract String jle();
    abstract String jg();
    abstract String jge();
    abstract String shl();
    abstract String shr();
    abstract String sar();
    abstract String neg();
    abstract String and();
    abstract String or();
    abstract String xor();
    abstract String not();
    abstract String cmp();
    abstract String test();
    abstract String cqto();
    abstract String call();

    // directives
    abstract String quad();

    // registers
    abstract String rax();
    abstract String rsp();
    abstract String rbp();
    abstract String rdi();
    abstract String rsi();
    abstract String rdx();
    abstract String rcx();
    abstract String r8();
    abstract String r9();
    abstract String r10();
    abstract String r11();
    abstract String rip();
    abstract String cl();
}
