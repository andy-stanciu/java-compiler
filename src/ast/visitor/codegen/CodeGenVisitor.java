package ast.visitor.codegen;

import ast.*;
import ast.visitor.Visitor;
import codegen.FlowContext;
import codegen.Generator;
import semantics.table.SymbolContext;
import semantics.type.TypeBoolean;
import semantics.type.TypeObject;

import java.util.function.Consumer;

public final class CodeGenVisitor implements Visitor {
    private final Generator generator;
    private final SymbolContext symbolContext;

    public CodeGenVisitor(SymbolContext symbolContext) {
        this.generator = Generator.getInstance();
        this.symbolContext = symbolContext;
    }

    @Override
    public void visit(Program n) {
        generator.genCodeSection();
        n.m.accept(this);
        n.cl.forEach(c -> c.accept(this));
    }

    @Override
    public void visit(MainClass n) {
        generator.genLabel("asm_main");
        generator.genPrologue();
        n.s.accept(this);
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
        throw new IllegalStateException();
    }

    @Override
    public void visit(MethodDecl n) {
        var method = symbolContext.lookupMethod(n.i.s);
        if (method == null) {
            throw new IllegalStateException();
        }

        generator.genLabel(method.getQualifiedName());
        generator.genPrologue();

        // allocate method stack frame
        generator.genBinary("subq", "$" + method.frameSize, "%rsp");

        // obj ptr is always first
        generator.genBinary("movq", "%rdi", -Generator.WORD_SIZE + "(%rbp)");

        symbolContext.enterMethod(n.i.s);
        // save parameters on the stack
        for (int i = 0; i < n.fl.size(); i++) {
            var p = symbolContext.lookupVariable(n.fl.get(i).i.s);
            if (p == null) {
                throw new IllegalStateException();
            }

            generator.genBinary("movq", generator.getArgumentRegister(i + 1),
                    p.getOffset() + "(%rbp)");
        }

        n.sl.forEach(s -> s.accept(this));
        n.e.accept(this);
        symbolContext.exit();

        generator.genEpilogue();
    }

    @Override
    public void visit(Formal n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntArrayType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BooleanType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntegerType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IdentifierType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Block n) {
        n.sl.forEach(s -> s.accept(this));
    }

    @Override
    public void visit(If n) {
        String elseLabel = generator.nextLabel("else");
        String endifLabel = generator.nextLabel("end_if");

        generator.push(new FlowContext(elseLabel, false));
        n.e.accept(this);  // bool expression

        n.s1.accept(this);
        generator.genUnary("jmp", endifLabel);
        generator.genLabel(elseLabel);
        n.s2.accept(this);
        generator.genLabel(endifLabel);
    }

    @Override
    public void visit(While n) {
        String testLabel = generator.nextLabel("while_test");
        String bodyLabel = generator.nextLabel("while");

        generator.genUnary("jmp", testLabel);
        generator.genLabel(bodyLabel);
        n.s.accept(this);
        generator.genLabel(testLabel);

        generator.push(new FlowContext(bodyLabel, true));
        n.e.accept(this);  // bool expression
    }

    @Override
    public void visit(For n) {
        // TODO: implement
        throw new IllegalStateException();
    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        generator.genBinary("movq", "%rax", "%rdi");
        generator.genCall("put");
    }

    @Override
    public void visit(AssignSimple n) {
        visitAssign(n, g -> {});
    }

    @Override
    public void visit(AssignPlus n) {
        visitAssign(n, g -> {
            g.genBinary("addq", "0(%rdx)", "%rax");  // add value to rax
        });
    }

    @Override
    public void visit(AssignMinus n) {
        visitAssign(n, g -> {
            g.genBinary("subq", "0(%rdx)", "%rax");    // subtract rcx from rax
        });
    }

    @Override
    public void visit(AssignTimes n) {
        visitAssign(n, g -> {
            g.genBinary("imulq", "0(%rdx)", "%rax");    // multiply rax by rcx
        });
    }

    @Override
    public void visit(AssignDivide n) {
        visitAssign(n, g -> {
            g.genBinary("movq", "$" + n.line_number, "%rdi");  // load line number in first arg
            g.genBinary("cmpq", "$0", "%rax");                 // check if divisor is 0
            g.genUnary("je", "exception_division");                // division by 0 error
            g.genPush("%rdx");                                          // push lvalue onto stack
            g.genPush("%rax");                                          // push expr (divisor) onto stack
            g.genBinary("movq", "0(%rdx)", "%rax");            // dereference rdx into rax
            g.gen("cqto");                                        // sign extend rax to rdx:rax
            g.genPop("%rcx");                                           // pop divisor into rcx
            g.genUnary("idivq", "%rcx");                           // divide rdx:rax by rcx, result in rax
            g.genPop("%rdx");                                          // pop lvalue back into rdx
        });
    }

    @Override
    public void visit(AssignMod n) {
        visitAssign(n, g -> {
            g.genBinary("movq", "$" + n.line_number, "%rdi");  // load line number in first arg
            g.genBinary("cmpq", "$0", "%rax");                 // check if divisor is 0
            g.genUnary("je", "exception_division");                // division by 0 error
            g.genPush("%rdx");                                          // push lvalue onto stack
            g.genPush("%rax");                                          // push expr (divisor) onto stack
            g.genBinary("movq", "0(%rdx)", "%rax");            // dereference rdx into rax
            g.gen("cqto");                                        // sign extend rax to rdx:rax
            g.genPop("%rcx");                                           // pop divisor into rcx
            g.genUnary("idivq", "%rcx");                           // divide rdx:rax by rcx, result in rax
            g.genBinary("movq", "%rdx", "%rax");               // move remainder into rax
            g.genPop("%rdx");                                          // pop lvalue back into rdx
        });
    }

    @Override
    public void visit(AssignAnd n) {
        visitAssign(n, g -> {
            g.genBinary("andq", "0(%rdx)", "%rax");  // bitwise and rax by rcx
        });
    }

    @Override
    public void visit(AssignOr n) {
        visitAssign(n, g -> {
            g.genBinary("orq", "0(%rdx)", "%rax");  // bitwise or rax by rcx
        });
    }

    @Override
    public void visit(AssignXor n) {
        visitAssign(n, g -> {
            g.genBinary("xorq", "0(%rdx)", "%rax");  // bitwise xor rax by rcx
        });
    }

    @Override
    public void visit(AssignLeftShift n) {
        visitAssign(n, g -> {
            g.genBinary("movq", "%rax", "%rcx");     // move lshift amount into rcx
            g.genBinary("movq", "0(%rdx)", "%rax");  // dereference rdx into rax
            g.genBinary("shlq", "%cl", "%rax");      // lshift rax by lshift amount
        });
    }

    @Override
    public void visit(AssignRightShift n) {
        visitAssign(n, g -> {
            g.genBinary("movq", "%rax", "%rcx");     // move rshift amount into rcx
            g.genBinary("movq", "0(%rdx)", "%rax");  // dereference rdx into rax
            g.genBinary("sarq", "%cl", "%rax");      // rshift rax by rshift amount
        });
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        visitAssign(n, g -> {
            g.genBinary("movq", "%rax", "%rcx");     // move urshift amount into rcx
            g.genBinary("movq", "0(%rdx)", "%rax");  // dereference rdx into rax
            g.genBinary("shrq", "%cl", "%rax");      // urshift rax by urshift amount
        });
    }

    @Override
    public void visit(PostIncrement n) {
        generator.signalAssignable();
        n.a.accept(this);
        generator.genBinary("addq", "$1", "0(%rax)");  // increment value by 1
    }

    @Override
    public void visit(PreIncrement n) {
        // since increments are statements (not expressions),
        // there is no difference between pre/post increment for now
        generator.signalAssignable();
        n.a.accept(this);
        generator.genBinary("addq", "$1", "0(%rax)");  // increment value by 1
    }

    @Override
    public void visit(PostDecrement n) {
        generator.signalAssignable();
        n.a.accept(this);
        generator.genBinary("subq", "$1", "0(%rax)");  // decrement value by 1
    }

    @Override
    public void visit(PreDecrement n) {
        // since increments are statements (not expressions),
        // there is no difference between pre/post decrement for now
        generator.signalAssignable();
        n.a.accept(this);
        generator.genBinary("subq", "$1", "0(%rax)");  // decrement value by 1
    }

    @Override
    public void visit(And n) {
        var context = generator.pop();
        String joinLabel = generator.nextLabel("join");

        n.e1.accept(this);
        generator.genBinary("cmpq", "$0", "%rax");
        if (context != null && !context.jumpIf()) {
            generator.genUnary("je", context.targetLabel());
        } else {
            generator.genUnary("je", joinLabel);
        }
        n.e2.accept(this);
        generator.genBinary("cmpq", "$0", "%rax");
        if (context != null) {
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
            }
        }
        generator.genLabel(joinLabel);
    }

    @Override
    public void visit(Or n) {
        var context = generator.pop();
        String joinLabel = generator.nextLabel("join");

        n.e1.accept(this);
        generator.genBinary("cmpq", "$0", "%rax");
        if (context != null && context.jumpIf()) {
            generator.genUnary("jne", context.targetLabel());
        } else {
            generator.genUnary("jne", joinLabel);
        }
        n.e2.accept(this);
        generator.genBinary("cmpq", "$0", "%rax");
        if (context != null) {
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
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
    public void visit(Plus n) {
        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genPop("%rdx");
        generator.genBinary("addq", "%rdx", "%rax");
    }

    @Override
    public void visit(Minus n) {
        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genPop("%rdx");
        generator.genBinary("movq", "%rax", "%rcx");
        generator.genBinary("subq", "%rcx", "%rdx");
        generator.genBinary("movq", "%rdx", "%rax");
    }

    @Override
    public void visit(Times n) {
        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genPop("%rdx");
        generator.genBinary("imulq", "%rdx", "%rax");
    }

    @Override
    public void visit(Divide n) {
        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genPop("%rdx");
        generator.genBinary("movq", "%rax", "%rcx");               // load divisor in rcx
        generator.genBinary("movq", "%rdx", "%rax");               // load dividend in rax
        generator.genBinary("movq", "$" + n.line_number, "%rdi");  // load line number in first arg
        generator.genBinary("cmpq", "$0", "%rcx");                 // check if divisor is 0
        generator.genUnary("je", "exception_division");                // division by 0 error
        generator.gen("cqto");                                        // sign extend rax to rdx:rax
        generator.genUnary("idivq", "%rcx");                           // divide rdx:rax by rcx, result in rax
    }

    @Override
    public void visit(Mod n) {
        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genPop("%rdx");
        generator.genBinary("movq", "%rax", "%rcx");               // load divisor in rcx
        generator.genBinary("movq", "%rdx", "%rax");               // load dividend in rax
        generator.genBinary("movq", "$" + n.line_number, "%rdi");  // load line number in first arg
        generator.genBinary("cmpq", "$0", "%rcx");                 // check if divisor is 0
        generator.genUnary("je", "exception_division");                // division by 0 error
        generator.gen("cqto");                                        // sign extend rax to rdx:rax
        generator.genUnary("idivq", "%rcx");                           // divide rdx:rax by rcx, result in rax
        generator.genBinary("movq", "%rdx", "%rax");               // move remainder into rax
    }

    @Override
    public void visit(LeftShift n) {
        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genBinary("movq", "%rax", "%rcx");  // move lshift amount into rcx
        generator.genPop("%rax");                              // pop value into rax
        generator.genBinary("shlq", "%cl", "%rax");   // lshift rax by lshift amount
    }

    @Override
    public void visit(RightShift n) {
        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genBinary("movq", "%rax", "%rcx");  // move rshift amount into rcx
        generator.genPop("%rax");                              // pop value into rax
        generator.genBinary("sarq", "%cl", "%rax");   // rshift rax by rshift amount
    }

    @Override
    public void visit(UnsignedRightShift n) {
        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genBinary("movq", "%rax", "%rcx");  // move urshift amount into rcx
        generator.genPop("%rax");                              // pop value into rax
        generator.genBinary("shrq", "%cl", "%rax");   // urshift rax by urshift amount
    }

    @Override
    public void visit(ArrayLookup n) {
        boolean assignable = generator.isAssignable();

        n.e1.accept(this);
        generator.genPush("%rax");                                          // push arr ptr onto stack
        n.e2.accept(this);
        generator.genPop("%rcx");                                           // pop arr ptr into rcx
        generator.genBinary("movq", "%rax", "%rdi");               // load index into rdi
        generator.genBinary("movq", "0(%rcx)", "%rsi");            // load sizeof(arr) into rsi
        generator.genBinary("movq", "$" + n.line_number, "%rdx");  // load line number into rdx
        generator.genBinary("cmpq", "$0", "%rdi");                 // check if index < 0
        generator.genUnary("jl", "exception_array");                   // array index out of bounds exception
        generator.genBinary("cmpq", "%rsi", "%rdi");               // check if index >= sizeof(arr)
        generator.genUnary("jge", "exception_array");                  // array index out of bounds exception
        generator.genBinary("addq", "$1", "%rdi");                // index++ (since size @ index 0)

        String instr = assignable ? "leaq" : "movq";
        generator.genBinary(instr, "(%rcx,%rdi," +                         // load arr[i] or &arr[i] into rax
                Generator.WORD_SIZE + ")", "%rax");
    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);
        generator.genBinary("movq", "0(%rax)", "%rax");
    }

    @Override
    public void visit(Call n) {
        var context = generator.pop();
        n.e.accept(this);
        generator.genPush("%rax");  // push obj ptr onto stack

        var class_ = ((TypeObject) n.e.type).base;
        var method = symbolContext.lookupMethod(n.i.s, class_);
        if (method == null) {
            throw new IllegalStateException();
        }

        // push args onto stack
        for (int i = 0; i < n.el.size(); i++) {
            n.el.get(i).accept(this);
            generator.genPush("%rax");
        }

        // pop args off stack
        for (int i = n.el.size(); i > 0; i--) {
            generator.genPop(generator.getArgumentRegister(i));
        }

        generator.genPop("%rdi");                                 // pop obj ptr off stack
        generator.genBinary("movq", "0(%rdi)", "%rax");   // load vtable addr
        generator.genCall("*" + method.getOffset() + "(%rax)");  // call method from vtable

        if (context != null) {
            if (!method.returnType.equals(TypeBoolean.getInstance())) {
                // sanity check that if we're in a flow context, it must be the
                // case that this method returns a boolean
                throw new IllegalStateException();
            }

            generator.genBinary("cmpq", "$0", "%rax");
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
            }
        }
    }

    @Override
    public void visit(Field n) {
        var context = generator.pop();
        boolean assignable = generator.isAssignable();
        n.e.accept(this);

        var class_ = ((TypeObject) n.e.type).base;
        var v = symbolContext.lookupInstanceVariable(n.i.s, class_);
        if (v == null || !v.isInstanceVariable()) {
            throw new IllegalStateException();
        }

        String instr = assignable ? "leaq" : "movq";
        generator.genBinary(instr, v.getOffset() + "(%rax)", "%rax");  // load var into rax

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

            generator.genBinary("cmpq", "$0", "%rax");
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
            }
        }
    }

    @Override
    public void visit(Ternary n) {
        var context = generator.pop();

        String ternaryElseLabel = generator.nextLabel("ternary_else");
        String endTernaryLabel = generator.nextLabel("end_ternary");

        n.c.accept(this);
        generator.genBinary("cmpq", "$0", "%rax");
        generator.genUnary("je", ternaryElseLabel);
        n.e1.accept(this);
        generator.genUnary("jmp", endTernaryLabel);
        generator.genLabel(ternaryElseLabel);
        n.e2.accept(this);
        generator.genLabel(endTernaryLabel);

        if (context != null) {
            if (!n.type.equals(TypeBoolean.getInstance())) {
                // sanity check that if we're in a flow context, it must be the
                // case that the ternary evaluates to a boolean
                throw new IllegalStateException();
            }

            generator.genBinary("cmpq", "$0", "%rax");
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
            }
        }
    }

    @Override
    public void visit(InstanceOf n) {
        var context = generator.pop();

        String instanceOfLabel = generator.nextLabel("top");
        String endInstanceOfLabel = generator.nextLabel("end_instance_of");

        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genBinary("leaq", class_.name + "$$(%rip)", "%rax");  // lea of vtable into rax
        generator.genPush("%rax");                                               // push vtable ptr on stack
        n.e.accept(this);
        generator.genPop("%rdx");                                                // pop vtable ptr into rdx
        generator.genLabel(instanceOfLabel);
        generator.genBinary("movq", "0(%rax)", "%rax");                 // load vtable ptr of obj
        generator.genBinary("cmpq", "$0", "%rax");                      // check if vtable ptr is null
        generator.genUnary("je", endInstanceOfLabel);                            // if null, jump to end (rax = 0)
        generator.genBinary("cmpq", "%rax", "%rdx");                    // compare vtable ptrs
        generator.genUnary("jne", instanceOfLabel);                              // if not equal, loop
        generator.genBinary("movq", "$1", "%rax");                      // vtable ptrs are equal
        generator.genLabel(endInstanceOfLabel);

        if (context != null) {
            generator.genBinary("cmpq", "$0", "%rax");
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
            }
        }
    }

    @Override
    public void visit(IntegerLiteral n) {
        generator.genBinary("movq", "$" + n.i, "%rax");
    }

    @Override
    public void visit(True n) {
        var context = generator.pop();
        generator.genBinary("movq", "$1", "%rax");
        if (context != null && context.jumpIf()) {
            generator.genUnary("jmp", context.targetLabel());
        }
    }

    @Override
    public void visit(False n) {
        var context = generator.pop();
        generator.genBinary("movq", "$0", "%rax");
        if (context != null && !context.jumpIf()) {
            generator.genUnary("jmp", context.targetLabel());
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

        String instr = assignable ? "leaq" : "movq";
        if (v.isInstanceVariable()) {
            generator.genBinary("movq", -Generator.WORD_SIZE + "(%rbp)", "%rdx");  // load obj ptr in rdx
            generator.genBinary(instr, v.getOffset() + "(%rdx)", "%rax");              // load var from obj
        } else {
            generator.genBinary(instr, v.getOffset() + "(%rbp)", "%rax");              // load var from frame
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

            generator.genBinary("cmpq", "$0", "%rax");
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
            }
        }
    }

    @Override
    public void visit(This n) {
        generator.genBinary("movq", -Generator.WORD_SIZE + "(%rbp)", "%rax");  // load obj ptr
    }

    @Override
    public void visit(NewArray n) {
        n.e.accept(this);
        generator.genPush("%rax");                               // push sizeof(arr) onto stack
        generator.genBinary("addq", "$1", "%rax");       // sizeof(arr) + 1 bytes
        generator.genBinary("shlq", "$3", "%rax");       // multiply size by word size (8)
        generator.genBinary("movq", "%rax", "%rdi");     // load heap size into first arg
        generator.genCall("mjcalloc");                          // allocate space on heap
        generator.genPop("%rdx");                                // pop sizeof(arr) off stack
        generator.genBinary("movq", "%rdx", "0(%rax)");  // store sizeof(arr) at start of array
    }

    @Override
    public void visit(NewObject n) {
        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genBinary("movq", "$" + class_.size(), "%rdi");       // load obj size into first arg
        generator.genCall("mjcalloc");                                         // allocate space on heap
        generator.genBinary("leaq", class_.name + "$$(%rip)", "%rdx");  // lea of vtable
        generator.genBinary("movq", "%rdx", "0(%rax)");                 // store vtable at start of obj
    }

    @Override
    public void visit(Not n) {
        var context = generator.pop();

        n.e.accept(this);
        generator.genBinary("xorq", "$1", "%rax");

        if (context != null) {
            generator.genBinary("cmpq", "$0", "%rax");
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
            }
        }
    }

    @Override
    public void visit(BitwiseNot n) {
        n.e.accept(this);
        generator.genUnary("notq", "%rax");
    }

    @Override
    public void visit(Identifier n) {
        var v = symbolContext.lookupVariable(n.s);
        if (v == null) {
            throw new IllegalStateException();
        }

        if (v.isInstanceVariable()) {
            generator.genBinary("movq", -Generator.WORD_SIZE + "(%rbp)", "%rdx");  // load obj ptr in rdx
            generator.genBinary("leaq", v.getOffset() + "(%rdx)", "%rax");         // lea of var in obj
        } else {
            generator.genBinary("leaq", v.getOffset() + "(%rbp)", "%rax");         // lea of var in frame
        }
    }

    @Override
    public void visit(NoOp n) {
        // TODO: implement
        throw new IllegalStateException();
    }

    @Override
    public void visit(NoOpExp n) {
        // TODO: implement
        throw new IllegalStateException();
    }

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
        n.a.accept(this);

        generator.genPush("%rax");                               // push addr of var onto stack
        n.e.accept(this);
        generator.genPop("%rdx");                                // pop lvalue off stack
        ops.accept(generator);                                       // apply assignment ops to rax
        generator.genBinary("movq", "%rax", "0(%rdx)");  // move rax to lvalue
    }

    /**
     * Visits the specified boolean-returning binary expression. Pops the flow
     * context, if any.
     * @param n The boolean-returning binary expression.
     */
    private void visitBinaryBoolExp(BinaryExp n) {
        var context = generator.pop();
        String trueLabel = generator.nextLabel("true");
        String joinLabel = generator.nextLabel("join");

        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genPop("%rdx");
        generator.genBinary("cmpq", "%rax", "%rdx");

        if (n instanceof LessThan) {
            generator.genUnary("jl", trueLabel);
        } else if (n instanceof LessThanOrEqual) {
            generator.genUnary("jle", trueLabel);
        } else if (n instanceof GreaterThan) {
            generator.genUnary("jg", trueLabel);
        } else if (n instanceof GreaterThanOrEqual) {
            generator.genUnary("jge", trueLabel);
        } else if (n instanceof Equal) {
            generator.genUnary("je", trueLabel);
        } else if (n instanceof NotEqual) {
            generator.genUnary("jne", trueLabel);
        } else {
            throw new IllegalStateException();
        }

        generator.genBinary("movq", "$0", "%rax");
        if (context != null && !context.jumpIf()) {
            generator.genUnary("jmp", context.targetLabel());
        } else {
            generator.genUnary("jmp", joinLabel);
        }
        generator.genLabel(trueLabel);
        generator.genBinary("movq", "$1", "%rax");
        if (context != null && context.jumpIf()) {
            generator.genUnary("jmp", context.targetLabel());
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
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genPop("%rdx");

        if (n instanceof BitwiseAnd) {
            generator.genBinary("andq", "%rdx", "%rax");
        } else if (n instanceof BitwiseOr) {
            generator.genBinary("orq", "%rdx", "%rax");
        } else if (n instanceof BitwiseXor) {
            generator.genBinary("xorq", "%rdx", "%rax");
        } else {
            throw new IllegalStateException();
        }

        if (context != null) {
            if (!n.type.equals(TypeBoolean.getInstance())) {
                // sanity check that if we're in a flow context, it must be the
                // case that this expression is operating on booleans
                throw new IllegalStateException();
            }

            generator.genBinary("cmpq", "$0", "%rax");
            if (!context.jumpIf()) {
                generator.genUnary("je", context.targetLabel());
            } else {
                generator.genUnary("jne", context.targetLabel());
            }
        }
    }
}
