package ast.visitor.codegen;

import ast.*;
import ast.visitor.Visitor;
import ast.visitor.util.FlowContext;
import codegen.BooleanContext;
import codegen.Generator;
import semantics.table.SymbolContext;
import semantics.type.TypeObject;

public class CodeGenVisitor implements Visitor {
    private final Generator generator;
    private final SymbolContext symbolContext;
    private final FlowContext flowContext;

    public CodeGenVisitor(SymbolContext symbolContext) {
        this.generator = Generator.getInstance();
        this.symbolContext = symbolContext;
        this.flowContext = FlowContext.create();
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

        flowContext.push(new BooleanContext(elseLabel, false));
        n.e.accept(this);

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

        flowContext.push(new BooleanContext(bodyLabel, true));
        n.e.accept(this);
    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        generator.genBinary("movq", "%rax", "%rdi");
        generator.genCall("put");
    }

    @Override
    public void visit(Assign n) {
        n.i.accept(this);
        generator.genPush("%rax");  // push addr of var onto stack

        var v = symbolContext.lookupVariable(n.i.s);
        if (v == null) {
            throw new IllegalStateException();
        }

        n.e.accept(this);
        generator.genPop("%rdx");  // pop lvalue off stack
        generator.genBinary("movq", "%rax", "0(%rdx)");
    }

    @Override
    public void visit(ArrayAssign n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(And n) {
        var context = flowContext.pop();
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
    public void visit(LessThan n) {
        var context = flowContext.pop();
        String joinLabel = generator.nextLabel("join");
        String trueLabel = generator.nextLabel("true");

        n.e1.accept(this);
        generator.genPush("%rax");
        n.e2.accept(this);
        generator.genPop("%rdx");
        generator.genBinary("cmpq", "%rax", "%rdx");
        generator.genUnary("jl", trueLabel);
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
    public void visit(ArrayLookup n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayLength n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);
        generator.genPush("%rax");  // push obj ptr onto stack

        var class_ = ((TypeObject)n.e.type).base;
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
        generator.genBinary("movq", "0(%rdi)", "%rax");  // load vtable addr
        generator.genCall("*" + method.getOffset() + "(%rax)");  // call method from vtable
    }

    @Override
    public void visit(IntegerLiteral n) {
        generator.genBinary("movq", "$" + n.i, "%rax");
    }

    @Override
    public void visit(True n) {
        var context = flowContext.pop();
        generator.genBinary("movq", "$1", "%rax");
        if (context != null && context.jumpIf()) {
            generator.genUnary("jmp", context.targetLabel());
        }
    }

    @Override
    public void visit(False n) {
        var context = flowContext.pop();
        generator.genBinary("movq", "$0", "%rax");
        if (context != null && !context.jumpIf()) {
            generator.genUnary("jmp", context.targetLabel());
        }
    }

    @Override
    public void visit(IdentifierExp n) {
        var v = symbolContext.lookupVariable(n.s);
        if (v == null) {
            throw new IllegalStateException();
        }

        if (v.isInstanceVariable()) {
            generator.genBinary("movq", -Generator.WORD_SIZE + "(%rbp)", "%rdx");  // load obj ptr in rdx
            generator.genBinary("movq", v.getOffset() + "(%rdx)", "%rax");         // load var from obj
        } else {
            generator.genBinary("movq", v.getOffset() + "(%rbp)", "%rax");         // load var from frame
        }
    }

    @Override
    public void visit(This n) {
        generator.genBinary("movq", -Generator.WORD_SIZE + "(%rbp)", "%rax");  // load obj ptr
    }

    @Override
    public void visit(NewArray n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NewObject n) {
        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genBinary("movq", "$" + class_.size(), "%rdi");       // load obj size into first arg
        generator.genCall("mjcalloc");                                          // allocate space on heap
        generator.genBinary("leaq", class_.name + "$$(%rip)", "%rdx");  // lea of vtable
        generator.genBinary("movq", "%rdx", "0(%rax)");                 // store vtable at start of obj
    }

    @Override
    public void visit(Not n) {
        var context = flowContext.pop();

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
}
