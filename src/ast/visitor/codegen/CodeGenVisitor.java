package ast.visitor.codegen;

import ast.*;
import ast.visitor.LazyVisitor;
import codegen.FlowContext;
import codegen.Generator;
import codegen.synth.SyntheticFunction;
import codegen.synth.SyntheticFunctionRegistry;
import codegen.platform.*;
import codegen.platform.isa.ISA;
import semantics.table.SymbolContext;
import semantics.type.TypeBoolean;
import semantics.type.TypeInt;
import semantics.type.TypeObject;
import semantics.type.TypeString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static codegen.platform.Operation.*;
import static codegen.platform.Register.*;

public final class CodeGenVisitor extends LazyVisitor {
    private final Generator generator;
    private final SyntheticFunctionRegistry syntheticFunctionRegistry;
    private final SymbolContext symbolContext;

    public CodeGenVisitor(SymbolContext symbolContext, ISA isa) {
        this.generator = Generator.getInstance(isa);
        this.syntheticFunctionRegistry = SyntheticFunctionRegistry.getInstance(isa);
        this.symbolContext = symbolContext;
    }

    @Override
    public void visit(Program n) {
        generator.genCodeSection();
        n.m.accept(this);
        n.cl.forEach(c -> c.accept(this));
        syntheticFunctionRegistry.generateAll();
    }

    @Override
    public void visit(MainClass n) {
        generator.genLabel(Label.of("asm_main"));
        generator.genPrologue();

        symbolContext.enterClass(n.i1.s);
        symbolContext.enterMethod("main");

        var main = symbolContext.getCurrentMethod();

        // allocate main method stack frame
        if (main.frameSize > 0) {
            generator.genBinary(SUB, Immediate.of(main.frameSize), RSP);
        }

        n.sl.forEach(s -> s.accept(this));

        symbolContext.exit();
        symbolContext.exit();

        generator.genLabel(Label.of("ret$" + main.getQualifiedName()));
        generator.genEpilogue();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(VarDecl n) {
        // variable is already declared in stack frame, nothing to do
    }

    @Override
    public void visit(VarInit n) {
        // variable declared in stack frame, but we need to assign to it
        var v = symbolContext.lookupVariable(n.i.s);
        if (v == null || v.isInstanceVariable()) {
            throw new IllegalStateException();
        }

        generator.genBinary(LEA, Memory.of(RBP, v.getOffset()), RAX);  // load var from frame
        generator.genPush(RAX);  // push lvalue onto stack
        n.e.accept(this);
        generator.genPop(RDX);  // pop lvalue into rdx
        generator.genBinary(MOV, RAX, Memory.of(RDX, 0));  // move result into lvalue
    }

    @Override
    public void visit(MethodDecl n) {
        var method = symbolContext.lookupMethod(n.i.s);
        if (method == null) {
            throw new IllegalStateException();
        }

        generator.genLabel(Label.of(method.getQualifiedName()));
        generator.genPrologue();

        // allocate method stack frame
        generator.genBinary(SUB, Immediate.of(method.frameSize), RSP);

        // obj ptr is always first
        generator.genBinary(MOV, RDI, Memory.of(RBP, -Generator.WORD_SIZE));

        symbolContext.enterMethod(n.i.s);
        // save parameters on the stack
        for (int i = 0; i < n.fl.size(); i++) {
            var p = symbolContext.lookupVariable(n.fl.get(i).i.s);
            if (p == null) {
                throw new IllegalStateException();
            }

            generator.genBinary(MOV, generator.getArgumentRegister(i + 1),
                    Memory.of(RBP, p.getOffset()));
        }

        n.sl.forEach(s -> s.accept(this));
        symbolContext.exit();

        generator.genLabel(Label.of("ret$" + method.getQualifiedName()));
        generator.genEpilogue();
    }

    @Override
    public void visit(Block n) {
        symbolContext.enterBlock(n.blockInfo);
        n.sl.forEach(s -> s.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(Return n) {
        var m = symbolContext.getCurrentMethod();
        if (m == null) {
            throw new IllegalStateException();
        }

        n.e.accept(this);
        generator.genUnary(JMP, Label.of("ret$" + m.getQualifiedName()));
    }

    @Override
    public void visit(If n) {
        var endifLabel = generator.nextLabel("end_if");

        generator.push(new FlowContext(endifLabel, false));
        n.e.accept(this);  // bool expression

        n.s.accept(this);
        generator.genLabel(endifLabel);
    }

    @Override
    public void visit(IfElse n) {
        var elseLabel = generator.nextLabel("else");
        var endifLabel = generator.nextLabel("end_if");

        generator.push(new FlowContext(elseLabel, false));
        n.e.accept(this);  // bool expression

        n.s1.accept(this);
        generator.genUnary(JMP, endifLabel);
        generator.genLabel(elseLabel);
        n.s2.accept(this);
        generator.genLabel(endifLabel);
    }

    @Override
    public void visit(Switch n) {
        n.e.accept(this);

        var endSwitchLabel = generator.nextLabel("end_switch");

        List<Label> cases = new ArrayList<>();
        CaseDefault d = null;
        int defaultIdx = -1;

        for (int i = 0; i < n.cl.size(); i++) {
            var c = n.cl.get(i);
            if (c instanceof CaseSimple case_) {
                var caseLabel = generator.nextLabel("case");
                cases.add(caseLabel);
                generator.genBinary(CMP, Immediate.of(case_.n), RAX);
                generator.genUnary(JE, caseLabel);
            } else if (c instanceof CaseDefault default_) {
                var defaultLabel = generator.nextLabel("default");
                cases.add(defaultLabel);
                d = default_;
                defaultIdx = i;
            }
        }

        if (d != null) {
            generator.genUnary(JMP, cases.get(defaultIdx));
        } else {
            generator.genUnary(JMP, endSwitchLabel);
        }

        for (int i = 0; i < n.cl.size(); i++) {
            var c = n.cl.get(i);
            if (c instanceof CaseSimple) {
                generator.genLabel(cases.get(i));
                c.accept(this);
                if (c.breaks || i + 1 == n.cl.size()) {
                    generator.genUnary(JMP, endSwitchLabel);
                } else if (i + 1 == defaultIdx && defaultIdx + 1 < cases.size()) {
                    generator.genUnary(JMP, cases.get(defaultIdx));
                }
            }
        }

        if (d != null) {
            generator.genLabel(cases.get(defaultIdx));
            d.accept(this);
            if (!d.breaks && defaultIdx + 1 < cases.size()) {
                generator.genUnary(JMP, cases.get(defaultIdx + 1));
            }
        }

        generator.genLabel(endSwitchLabel);
    }

    @Override
    public void visit(CaseSimple n) {
        n.sl.forEach(s -> s.accept(this));
    }

    @Override
    public void visit(CaseDefault n) {
        n.sl.forEach(s -> s.accept(this));
    }

    @Override
    public void visit(While n) {
        var testLabel = generator.nextLabel("while_test");
        var bodyLabel = generator.nextLabel("while");

        generator.genUnary(JMP, testLabel);
        generator.genLabel(bodyLabel);
        n.s.accept(this);
        generator.genLabel(testLabel);
        generator.push(new FlowContext(bodyLabel, true));
        n.e.accept(this);  // bool expression
    }

    @Override
    public void visit(For n) {
        var testLabel = generator.nextLabel("for_test");
        var bodyLabel = generator.nextLabel("for");

        symbolContext.enterBlock(n.blockInfo);
        n.s0.accept(this);  // initializer instructions
        generator.genUnary(JMP, testLabel);
        generator.genLabel(bodyLabel);
        n.s2.accept(this);  // body instructions
        n.s1.accept(this);  // incrementer instructions
        generator.genLabel(testLabel);
        generator.push(new FlowContext(bodyLabel, true));
        n.e.accept(this);  // condition expression
        symbolContext.exit();
    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        if (n.e.eval().type.equals(TypeInt.getInstance())) {
            generator.genBinary(MOV, RAX, RDI);
            generator.genCall(CFunction.PRINT);
        } else if (n.e.eval().type.equals(TypeString.getInstance())) {
            var printLabel = generator.nextLabel("print");
            var printTest = generator.nextLabel("print_test");
            generator.genBinary(MOV, Immediate.of(1), RDX);  // string index in rdx
            generator.genBinary(MOV, Memory.of(RAX, 0), RCX);  // load len(str) in rcx
            generator.genUnary(JMP, printTest);
            generator.genLabel(printLabel);
            generator.genBinary(MOV, MemoryScaledIndex.of(RAX, RDX, Generator.WORD_SIZE, 0), RDI);  // deref str[i] into rdi
            generator.genPush(RAX);  // save str
            generator.genPush(RDX);  // save i
            generator.genPush(RCX);  // save len(str)
            generator.genCall(CFunction.PRINTC);  // call printc with str[i] in rdi
            generator.genPop(RCX);  // restore len(str)
            generator.genPop(RDX);  // restore i
            generator.genPop(RAX);  // restore str
            generator.genBinary(ADD, Immediate.of(1), RDX);  // i++
            generator.genLabel(printTest);
            generator.genBinary(CMP, RCX, RDX);
            generator.genUnary(JLE, printLabel);
            generator.genBinary(MOV, Immediate.of('\n'), RDI);
            generator.genCall(CFunction.PRINTC);  // print newline
        } else if (n.e.eval().type.equals(TypeBoolean.getInstance())) {
            generator.genBinary(MOV, RAX, RDI);
            generator.genCall(CFunction.PRINTB);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visit(AssignSimple n) {
        visitAssign(n, g -> {});
    }

    @Override
    public void visit(AssignPlus n) {
        visitAssign(n, g -> {
            if (n.e1.eval().type.equals(TypeString.getInstance())) {
                g.genPush(RDX);
                g.genBinary(MOV, Memory.of(RDX, 0), RDI);
                g.genBinary(MOV, RAX, RSI);
                if (n.e2.eval().type.equals(TypeString.getInstance())) {
                    g.genCall(SyntheticFunction.CONCAT_STRING_STRING);
                } else if (n.e2.eval().type.equals(TypeInt.getInstance())) {
                    g.genCall(SyntheticFunction.CONCAT_STRING_INT);
                } else if (n.e2.eval().type.equals(TypeBoolean.getInstance())) {
                    g.genCall(SyntheticFunction.CONCAT_STRING_BOOL);
                } else {
                    throw new IllegalStateException();
                }
                g.genPop(RDX);
            } else {
                g.genBinary(ADD, Memory.of(RDX, 0), RAX);  // add value to rax
            }
        });
    }

    @Override
    public void visit(AssignMinus n) {
        visitAssign(n, g -> {
            g.genBinary(SUB, Memory.of(RDX, 0), RAX);  // subtract rcx from rax
        });
    }

    @Override
    public void visit(AssignTimes n) {
        visitAssign(n, g -> {
            g.genBinary(IMUL, Memory.of(RDX, 0), RAX);  // multiply rax by rcx
        });
    }

    @Override
    public void visit(AssignDivide n) {
        visitAssign(n, g -> {
            g.genBinary(MOV, Immediate.of(n.line_number), RDI);  // load line number in first arg
            g.genBinary(CMP, Immediate.of(0), RAX);  // check if divisor is 0
            g.genUnary(JE, Label.of("exception_division"));  // division by 0 error
            g.genPush(RDX);  // push lvalue onto stack
            g.genPush(RAX);  // push expr (divisor) onto stack
            g.genBinary(MOV, Memory.of(RDX, 0), RAX);  // dereference rdx into rax
            g.gen(CQTO);  // sign extend rax to rdx:rax
            g.genPop(RCX);  // pop divisor into rcx
            g.genUnary(IDIV, RCX);  // divide rdx:rax by rcx, result in rax
            g.genPop(RDX);  // pop lvalue back into rdx
        });
    }

    @Override
    public void visit(AssignMod n) {
        visitAssign(n, g -> {
            g.genBinary(MOV, Immediate.of(n.line_number), RDI);  // load line number in first arg
            g.genBinary(CMP, Immediate.of(0), RAX);  // check if divisor is 0
            g.genUnary(JE, Label.of("exception_division"));  // division by 0 error
            g.genPush(RDX);  // push lvalue onto stack
            g.genPush(RAX);  // push expr (divisor) onto stack
            g.genBinary(MOV, Memory.of(RDX, 0), RAX);  // dereference rdx into rax
            g.gen(CQTO);  // sign extend rax to rdx:rax
            g.genPop(RCX);  // pop divisor into rcx
            g.genUnary(IDIV, RCX);  // divide rdx:rax by rcx, result in rax
            g.genBinary(MOV, RDX, RAX);  // move remainder into rax
            g.genPop(RDX);  // pop lvalue back into rdx
        });
    }

    @Override
    public void visit(AssignAnd n) {
        visitAssign(n, g -> {
            g.genBinary(AND, Memory.of(RDX, 0), RAX);  // bitwise and rax by rcx
        });
    }

    @Override
    public void visit(AssignOr n) {
        visitAssign(n, g -> {
            g.genBinary(OR, Memory.of(RDX, 0), RAX);  // bitwise or rax by rcx
        });
    }

    @Override
    public void visit(AssignXor n) {
        visitAssign(n, g -> {
            g.genBinary(XOR, Memory.of(RDX, 0), RAX);  // bitwise xor rax by rcx
        });
    }

    @Override
    public void visit(AssignLeftShift n) {
        visitAssign(n, g -> {
            g.genBinary(MOV, RAX, RCX);  // move lshift amount into rcx
            g.genBinary(MOV, Memory.of(RDX, 0), RAX);  // dereference rdx into rax
            g.genBinary(SHL, CL, RAX);  // lshift rax by lshift amount
        });
    }

    @Override
    public void visit(AssignRightShift n) {
        visitAssign(n, g -> {
            g.genBinary(MOV, RAX, RCX);  // move rshift amount into rcx
            g.genBinary(MOV, Memory.of(RDX, 0), RAX);  // dereference rdx into rax
            g.genBinary(SAR, CL, RAX);  // rshift rax by rshift amount
        });
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        visitAssign(n, g -> {
            g.genBinary(MOV, RAX, RCX);  // move urshift amount into rcx
            g.genBinary(MOV, Memory.of(RDX, 0), RAX);  // dereference rdx into rax
            g.genBinary(SHR, CL, RAX);  // urshift rax by urshift amount
        });
    }

    @Override
    public void visit(PostIncrement n) {
        generator.signalAssignable();
        n.e.accept(this);
        generator.genBinary(MOV, Memory.of(RAX, 0), RDX);  // load value into rdx
        generator.genBinary(ADD, Immediate.of(1), Memory.of(RAX, 0));  // increment value by 1
        generator.genBinary(MOV, RDX, RAX);  // move initial value into rax
    }

    @Override
    public void visit(PreIncrement n) {
        generator.signalAssignable();
        n.e.accept(this);
        generator.genBinary(ADD, Immediate.of(1), Memory.of(RAX, 0));  // increment value by 1
        generator.genBinary(MOV, Memory.of(RAX, 0), RAX);  // load value into rax
    }

    @Override
    public void visit(PostDecrement n) {
        generator.signalAssignable();
        n.e.accept(this);
        generator.genBinary(MOV, Memory.of(RAX, 0), RDX);  // load value into rdx
        generator.genBinary(SUB, Immediate.of(1), Memory.of(RAX, 0));  // decrement value by 1
        generator.genBinary(MOV, RDX, RAX);  // move initial value into rax
    }

    @Override
    public void visit(PreDecrement n) {
        generator.signalAssignable();
        n.e.accept(this);
        generator.genBinary(SUB, Immediate.of(1), Memory.of(RAX, 0));  // decrement value by 1
        generator.genBinary(MOV, Memory.of(RAX, 0), RAX);  // load value into rax
    }

    @Override
    public void visit(And n) {
        var context = generator.pop();
        var joinLabel = generator.nextLabel("join");

        n.e1.accept(this);
        generator.genBinary(CMP, Immediate.of(0), RAX);
        if (context != null && !context.jumpIf()) {
            generator.genUnary(JE, context.targetLabel());
        } else {
            generator.genUnary(JE, joinLabel);
        }
        n.e2.accept(this);
        generator.genBinary(CMP, Immediate.of(0), RAX);
        if (context != null) {
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
        generator.genLabel(joinLabel);
    }

    @Override
    public void visit(Or n) {
        var context = generator.pop();
        var joinLabel = generator.nextLabel("join");

        n.e1.accept(this);
        generator.genBinary(CMP, Immediate.of(0), RAX);
        if (context != null && context.jumpIf()) {
            generator.genUnary(JNE, context.targetLabel());
        } else {
            generator.genUnary(JNE, joinLabel);
        }
        n.e2.accept(this);
        generator.genBinary(CMP, Immediate.of(0), RAX);
        if (context != null) {
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
        generator.genLabel(joinLabel);
    }

    @Override
    public void visit(Equal n) {
        visitBinaryBoolExp(n);
    }

    @Override
    public void visit(NotEqual n) {
        visitBinaryBoolExp(n);
    }

    @Override
    public void visit(LessThan n) {
        visitBinaryBoolExp(n);
    }

    @Override
    public void visit(LessThanOrEqual n) {
        visitBinaryBoolExp(n);
    }

    @Override
    public void visit(GreaterThan n) {
        visitBinaryBoolExp(n);
    }

    @Override
    public void visit(GreaterThanOrEqual n) {
        visitBinaryBoolExp(n);
    }

    @Override
    public void visit(BitwiseAnd n) {
        visitBinaryBitwiseExp(n);
    }

    @Override
    public void visit(BitwiseOr n) {
        visitBinaryBitwiseExp(n);
    }

    @Override
    public void visit(BitwiseXor n) {
        visitBinaryBitwiseExp(n);
    }

    @Override
    public void visit(UnaryMinus n) {
        n.e.accept(this);
        generator.genUnary(NEG, RAX);
    }

    @Override
    public void visit(UnaryPlus n) {
        n.e.accept(this);
    }

    @Override
    public void visit(Plus n) {
        var lhs = n.e1.eval().type;
        var rhs = n.e2.eval().type;

        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genPop(RDI);

        if (lhs.equals(TypeInt.getInstance()) && rhs.equals(TypeInt.getInstance())) {
            // int + int
            generator.genBinary(ADD, RDI, RAX);
        } else if (lhs.equals(TypeString.getInstance()) && rhs.equals(TypeString.getInstance())) {
            // string + string
            generator.genBinary(MOV, RAX, RSI);
            generator.genCall(SyntheticFunction.CONCAT_STRING_STRING);
        } else if (lhs.equals(TypeString.getInstance()) && rhs.equals(TypeBoolean.getInstance())) {
            // string + bool
            generator.genBinary(MOV, RAX, RSI);
            generator.genCall(SyntheticFunction.CONCAT_STRING_BOOL);
        } else if (lhs.equals(TypeBoolean.getInstance()) && rhs.equals(TypeString.getInstance())) {
            // bool + string
            generator.genBinary(MOV, RAX, RSI);
            generator.genCall(SyntheticFunction.CONCAT_BOOL_STRING);
        } else if (lhs.equals(TypeString.getInstance()) && rhs.equals(TypeInt.getInstance())) {
            // string + int
            generator.genBinary(MOV, RAX, RSI);
            generator.genCall(SyntheticFunction.CONCAT_STRING_INT);
        } else if (lhs.equals(TypeInt.getInstance()) && rhs.equals(TypeString.getInstance())) {
            // int + string
            generator.genBinary(MOV, RAX, RSI);
            generator.genCall(SyntheticFunction.CONCAT_INT_STRING);
        } else {
            // no other type combinations should have gotten through at this point
            throw new IllegalStateException();
        }
    }

    @Override
    public void visit(Minus n) {
        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genPop(RDX);
        generator.genBinary(MOV, RAX, RCX);
        generator.genBinary(SUB, RCX, RDX);
        generator.genBinary(MOV, RDX, RAX);
    }

    @Override
    public void visit(Times n) {
        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genPop(RDX);
        generator.genBinary(IMUL, RDX, RAX);
    }

    @Override
    public void visit(Divide n) {
        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genPop(RDX);
        generator.genBinary(MOV, RAX, RCX);  // load divisor in rcx
        generator.genBinary(MOV, RDX, RAX);  // load dividend in rax
        generator.genBinary(MOV, Immediate.of(n.line_number), RDI);  // load line number in first arg
        generator.genBinary(CMP, Immediate.of(0), RCX);  // check if divisor is 0
        generator.genUnary(JE, Label.of("exception_division"));  // division by 0 error
        generator.gen(CQTO);  // sign extend rax to rdx:rax
        generator.genUnary(IDIV, RCX);  // divide rdx:rax by rcx, result in rax
    }

    @Override
    public void visit(Mod n) {
        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genPop(RDX);
        generator.genBinary(MOV, RAX, RCX);  // load divisor in rcx
        generator.genBinary(MOV, RDX, RAX);  // load dividend in rax
        generator.genBinary(MOV, Immediate.of(n.line_number), RDI);  // load line number in first arg
        generator.genBinary(CMP, Immediate.of(0), RCX);  // check if divisor is 0
        generator.genUnary(JE, Label.of("exception_division"));  // division by 0 error
        generator.gen(CQTO);  // sign extend rax to rdx:rax
        generator.genUnary(IDIV, RCX);  // divide rdx:rax by rcx, result in rax
        generator.genBinary(MOV, RDX, RAX);  // move remainder into rax
    }

    @Override
    public void visit(LeftShift n) {
        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genBinary(MOV, RAX, RCX);  // move lshift amount into rcx
        generator.genPop(RAX);  // pop value into rax
        generator.genBinary(SHL, CL, RAX);  // lshift rax by lshift amount
    }

    @Override
    public void visit(RightShift n) {
        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genBinary(MOV, RAX, RCX);  // move rshift amount into rcx
        generator.genPop(RAX);  // pop value into rax
        generator.genBinary(SAR, CL, RAX);   // rshift rax by rshift amount
    }

    @Override
    public void visit(UnsignedRightShift n) {
        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genBinary(MOV, RAX, RCX);  // move urshift amount into rcx
        generator.genPop(RAX);  // pop value into rax
        generator.genBinary(SHR, CL, RAX);   // urshift rax by urshift amount
    }

    @Override
    public void visit(ArrayLookup n) {
        boolean assignable = generator.isAssignable();

        n.e1.accept(this);
        generator.genPush(RAX);  // push arr ptr
        for (int i = 0; i < n.getDimensionCount(); i++) {
            n.el.get(i).accept(this);
            generator.genBinary(MOV, RAX, RDI);  // move i to rdi
            generator.genPop(RCX);  // pop arr ptr into rcx
            generator.genBinary(MOV, Memory.of(RCX, 0), RSI);  // load sizeof(arr) into rsi
            generator.genBinary(MOV, Immediate.of(n.el.line_number), RDX);  // load line number into rdx
            generator.genBinary(CMP, Immediate.of(0), RDI);
            generator.genUnary(JL, Label.of("exception_array"));
            generator.genBinary(CMP, RSI, RDI);
            generator.genUnary(JGE, Label.of("exception_array"));
            generator.genBinary(ADD, Immediate.of(1), RDI);  // i++ due to size
            Operation op = (assignable && i == n.getDimensionCount() - 1) ? LEA : MOV;
            generator.genBinary(op, MemoryScaledIndex.of(RCX, RDI, Generator.WORD_SIZE, 0), RCX);  // load arr[i] or &arr[i] into rcx
            generator.genPush(RCX);  // push arr[i]/&arr[i] onto stack
        }
        generator.genPop(RAX);  // pop arr ptr
    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);
        generator.genBinary(MOV, Memory.of(RAX, 0), RAX);
    }

    @Override
    public void visit(Action n) {
        n.c.accept(this);
    }

    @Override
    public void visit(Call n) {
        var context = generator.pop();
        n.e.accept(this);
        generator.genPush(RAX);  // push obj ptr onto stack

        var class_ = ((TypeObject) n.e.eval().type).base;
        var method = symbolContext.lookupMethod(n.i.s, class_);
        if (method == null) {
            throw new IllegalStateException();
        }

        // push args onto stack
        for (int i = 0; i < n.el.size(); i++) {
            n.el.get(i).accept(this);
            generator.genPush(RAX);
        }

        // pop args off stack
        for (int i = n.el.size(); i > 0; i--) {
            generator.genPop(generator.getArgumentRegister(i));
        }

        generator.genPop(RDI);  // pop obj ptr off stack
        generator.genBinary(MOV, Memory.of(RDI, 0), RAX);  // load vtable addr
        generator.genCall(Memory.of(RAX, method.getOffset()));  // call method from vtable

        if (context != null) {
            if (!method.returnType.equals(TypeBoolean.getInstance())) {
                // sanity check that if we're in a flow context, it must be the
                // case that this method returns a boolean
                throw new IllegalStateException();
            }

            generator.genBinary(CMP, Immediate.of(0), RAX);
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
    }

    @Override
    public void visit(Field n) {
        var context = generator.pop();
        boolean assignable = generator.isAssignable();
        n.e.accept(this);

        var class_ = ((TypeObject) n.e.eval().type).base;
        var v = symbolContext.lookupInstanceVariable(n.i.s, class_);
        if (v == null || !v.isInstanceVariable()) {
            throw new IllegalStateException();
        }

        Operation op = assignable ? LEA : MOV;
        generator.genBinary(op, Memory.of(RAX, v.getOffset()), RAX);  // load var into rax

        if (context != null) {
            if (!v.type.equals(TypeBoolean.getInstance())) {
                // sanity check that if we're in a flow context, it must be the
                // case that this method returns a boolean
                throw new IllegalStateException();
            }

            if (assignable) {
                // impossible to be an assignable and be involved in a flow context
                // (for now!!)
                throw new IllegalStateException();
            }

            generator.genBinary(CMP, Immediate.of(0), RAX);
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
    }

    @Override
    public void visit(Ternary n) {
        var context = generator.pop();

        var ternaryElseLabel = generator.nextLabel("ternary_else");
        var endTernaryLabel = generator.nextLabel("end_ternary");

        n.c.accept(this);
        generator.genBinary(CMP, Immediate.of(0), RAX);
        generator.genUnary(JE, ternaryElseLabel);
        n.e1.accept(this);
        generator.genUnary(JMP, endTernaryLabel);
        generator.genLabel(ternaryElseLabel);
        n.e2.accept(this);
        generator.genLabel(endTernaryLabel);

        if (context != null) {
            if (!n.type.equals(TypeBoolean.getInstance())) {
                // sanity check that if we're in a flow context, it must be the
                // case that the ternary evaluates to a boolean
                throw new IllegalStateException();
            }

            generator.genBinary(CMP, Immediate.of(0), RAX);
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
    }

    @Override
    public void visit(InstanceOf n) {
        var context = generator.pop();

        var instanceOfLabel = generator.nextLabel("top");
        var endInstanceOfLabel = generator.nextLabel("end_instance_of");

        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genBinary(LEA, Memory.of(RIP, "_" + class_.name + "$$"), RAX);  // lea of vtable into rax
        generator.genPush(RAX);  // push vtable ptr on stack
        n.e.accept(this);
        generator.genPop(RDX);  // pop vtable ptr into rdx
        generator.genLabel(instanceOfLabel);
        generator.genBinary(MOV, Memory.of(RAX, 0), RAX);  // load vtable ptr of obj
        generator.genBinary(CMP, Immediate.of(0), RAX);  // check if vtable ptr is null
        generator.genUnary(JE, endInstanceOfLabel);  // if null, jump to end (rax = 0)
        generator.genBinary(CMP, RAX, RDX);  // compare vtable ptrs
        generator.genUnary(JNE, instanceOfLabel);  // if not equal, loop
        generator.genBinary(MOV, Immediate.of(1), RAX);  // vtable ptrs are equal
        generator.genLabel(endInstanceOfLabel);

        if (context != null) {
            generator.genBinary(CMP, Immediate.of(0), RAX);
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
    }

    @Override
    public void visit(IntegerLiteral n) {
        generator.genBinary(MOV, Immediate.of(n.i), RAX);
    }

    @Override
    public void visit(StringLiteral n) {
        // similar to new object: alloc string
        int length = (n.s.length() + 1) * Generator.WORD_SIZE;
        // load str length + 1 into rdi, including len(str)
        generator.genBinary(MOV, Immediate.of(length), RDI);
        generator.genCall(CFunction.MALLOC);
        // put len(str) at the start
        generator.genBinary(MOV, Immediate.of(n.s.length()), Memory.of(RAX, 0));
        for (int i = 0; i < n.s.length(); i++) {
            int c = n.s.charAt(i);
            generator.genBinary(MOV, Immediate.of(c), 
                Memory.of(RAX, (i + 1) * Generator.WORD_SIZE));
        }
        // resulting string pointer is left in rax
    }

    @Override
    public void visit(True n) {
        var context = generator.pop();
        generator.genBinary(MOV, Immediate.of(1), RAX);
        if (context != null && context.jumpIf()) {
            generator.genUnary(JMP, context.targetLabel());
        }
    }

    @Override
    public void visit(False n) {
        var context = generator.pop();
        generator.genBinary(MOV, Immediate.of(0), RAX);
        if (context != null && !context.jumpIf()) {
            generator.genUnary(JMP, context.targetLabel());
        }
    }

    @Override
    public void visit(IdentifierExp n) {
        var context = generator.pop();
        boolean assignable = generator.isAssignable();
        var v = symbolContext.lookupVariable(n.s);
        if (v == null) {
            throw new IllegalStateException();
        }

        Operation op = assignable ? LEA : MOV;
        if (v.isInstanceVariable()) {
            generator.genBinary(MOV, Memory.of(RBP, -Generator.WORD_SIZE), RDX);  // load obj ptr in rdx
            generator.genBinary(op, Memory.of(RDX, v.getOffset()), RAX);  // load var from obj
        } else {
            generator.genBinary(op, Memory.of(RBP, v.getOffset()), RAX);  // load var from frame
        }

        if (context != null) {
            if (v.type != TypeBoolean.getInstance()) {
                // sanity check that if we're in a flow context, it must be the
                // case that this variable has type boolean
                throw new IllegalStateException();
            }

            if (assignable) {
                // impossible to be an assignable and be involved in a flow context
                // (for now!!)
                throw new IllegalStateException();
            }

            generator.genBinary(CMP, Immediate.of(0), RAX);
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
    }

    @Override
    public void visit(This n) {
        generator.genBinary(MOV, Memory.of(RBP, -Generator.WORD_SIZE), RAX);  // load obj ptr
    }

    @Override
    public void visit(NewArray n) {
        n.el.get(0).accept(this);
        generator.genBinary(MOV, RAX, RDI);
        generator.genBinary(MOV, Immediate.of(n.el.line_number), RSI);
        generator.genBinary(CMP, Immediate.of(0), RDI);
        generator.genUnary(JL, Label.of("exception_array_size"));

        if (n.getDimensionCount() == 1) {
            generator.genCall(SyntheticFunction.ALLOCATE_ARRAY);
        } else {
            generator.genPush(RDI);
            generator.genCall(SyntheticFunction.ALLOCATE_ARRAY);
            generator.genPush(RAX);
            for (int i = 1; i < n.getDimensionCount(); i++) {
                n.el.get(i).accept(this);
                generator.genBinary(MOV, RAX, RDI);
                generator.genBinary(MOV, Immediate.of(n.el.line_number), RSI);
                generator.genBinary(CMP, Immediate.of(0), RDI);
                generator.genUnary(JL, Label.of("exception_array_size"));
                generator.genPush(RDI);
            }

            generator.clearArgumentRegisters();
            for (int i = n.getDimensionCount() - 1; i > 0; i--) {
                generator.genPop(generator.getArgumentRegister(i));
            }

            generator.genPop(RAX);
            generator.genPop(RDI);
            generator.genCall(SyntheticFunction.ALLOCATE_NESTED_ARRAY);
        }
    }

    @Override
    public void visit(NewObject n) {
        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genBinary(MOV, Immediate.of(class_.size()), RDI);  // load obj size into first arg
        generator.genCall(CFunction.MALLOC);  // allocate space on heap
        generator.genBinary(LEA, Memory.of(RIP, "_" + class_.name + "$$"), RDX);  // lea of vtable
        generator.genBinary(MOV, RDX, Memory.of(RAX, 0));  // store vtable at start of obj
        generator.genBinary(MOV, RAX, RDX);  // move obj to rdx

        // TODO: this is not gonna work. need to jump to ctor, which means we might as well implement constructors.
        // TODO: before constructor logic can be invoked, we apply all initializers to instance variables.
        symbolContext.swap(class_.name);
        class_.getInstanceVariables().forEach(v -> {
            if (v.hasInitializer()) {
                generator.genPush(RDX);  // push obj onto stack
                v.initializer.accept(this);
                generator.genPop(RDX);  // pop obj into rdx
                generator.genBinary(MOV, RAX, Memory.of(RDX, v.getOffset()));  // move result into obj
            }
        });
        symbolContext.restore();

        generator.genBinary(MOV, RDX, RAX);  // move obj back to rax
    }

    @Override
    public void visit(Not n) {
        var context = generator.pop();

        n.e.accept(this);
        generator.genBinary(XOR, Immediate.of(1), RAX);

        if (context != null) {
            generator.genBinary(CMP, Immediate.of(0), RAX);
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
    }

    @Override
    public void visit(BitwiseNot n) {
        n.e.accept(this);
        generator.genUnary(NOT, RAX);
    }

    @Override
    public void visit(Identifier n) {
        var v = symbolContext.lookupVariable(n.s);
        if (v == null) {
            throw new IllegalStateException();
        }

        if (v.isInstanceVariable()) {
            generator.genBinary(MOV, Memory.of(RBP, -Generator.WORD_SIZE), RDX);  // load obj ptr in rdx
            generator.genBinary(LEA, Memory.of(RDX, v.getOffset()), RAX);  // lea of var in obj
        } else {
            generator.genBinary(LEA, Memory.of(RBP, v.getOffset()), RAX);  // lea of var in frame
        }
    }

    @Override
    public void visit(NoOp n) {}

    @Override
    public void visit(NoOpExp n) {}

    /**
     * Visits the specified assignment statement. Leaves the assignable address
     * (lvalue) in <code>%rdx</code> and the expression in <code>%rax</code>,
     * and then accepts the specified pre-assignment operations. Then, assigns
     * to the lvalue.
     *
     * @param n   The assignment statement.
     * @param ops The pre-assignment operations. These operations must leave
     *            <code>%rdx</code> unchanged, and must leave their result in
     *            <code>%rax</code>.
     */
    private void visitAssign(Assign n, Consumer<Generator> ops) {
        generator.signalAssignable();
        n.e1.accept(this);

        generator.genPush(RAX);  // push addr of var onto stack
        n.e2.accept(this);
        generator.genPop(RDX);  // pop lvalue off stack
        ops.accept(generator);  // apply assignment ops to rax
        generator.genBinary(MOV, RAX, Memory.of(RDX, 0));  // move rax to lvalue
    }

    /**
     * Visits the specified boolean-returning binary expression. Pops the flow
     * context, if any.
     * @param n The boolean-returning binary expression.
     */
    private void visitBinaryBoolExp(BinaryExp n) {
        var context = generator.pop();
        var trueLabel = generator.nextLabel("true");
        var joinLabel = generator.nextLabel("join");

        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genPop(RDX);
        generator.genBinary(CMP, RAX, RDX);

        if (n instanceof LessThan) {
            generator.genUnary(JL, trueLabel);
        } else if (n instanceof LessThanOrEqual) {
            generator.genUnary(JLE, trueLabel);
        } else if (n instanceof GreaterThan) {
            generator.genUnary(JG, trueLabel);
        } else if (n instanceof GreaterThanOrEqual) {
            generator.genUnary(JGE, trueLabel);
        } else if (n instanceof Equal) {
            generator.genUnary(JE, trueLabel);
        } else if (n instanceof NotEqual) {
            generator.genUnary(JNE, trueLabel);
        } else {
            throw new IllegalStateException();
        }

        generator.genBinary(MOV, Immediate.of(0), RAX);
        if (context != null && !context.jumpIf()) {
            generator.genUnary(JMP, context.targetLabel());
        } else {
            generator.genUnary(JMP, joinLabel);
        }
        generator.genLabel(trueLabel);
        generator.genBinary(MOV, Immediate.of(1), RAX);
        if (context != null && context.jumpIf()) {
            generator.genUnary(JMP, context.targetLabel());
        }
        generator.genLabel(joinLabel);
    }

    /**
     * Visits the specified bitwise binary expression. Pops the flow
     * context, if any.
     * @param n The bitwise binary expression.
     */
    private void visitBinaryBitwiseExp(BinaryExp n) {
        var context = generator.pop();

        n.e1.accept(this);
        generator.genPush(RAX);
        n.e2.accept(this);
        generator.genPop(RDX);

        if (n instanceof BitwiseAnd) {
            generator.genBinary(AND, RDX, RAX);
        } else if (n instanceof BitwiseOr) {
            generator.genBinary(OR, RDX, RAX);
        } else if (n instanceof BitwiseXor) {
            generator.genBinary(XOR, RDX, RAX);
        } else {
            throw new IllegalStateException();
        }

        if (context != null) {
            if (!n.type.equals(TypeBoolean.getInstance())) {
                // sanity check that if we're in a flow context, it must be the
                // case that this expression is operating on booleans
                throw new IllegalStateException();
            }

            generator.genBinary(CMP, Immediate.of(0), RAX);
            if (!context.jumpIf()) {
                generator.genUnary(JE, context.targetLabel());
            } else {
                generator.genUnary(JNE, context.targetLabel());
            }
        }
    }
}
