package ast.visitor.codegen;

import ast.*;
import ast.visitor.Visitor;
import codegen.Generator;
import semantics.table.SymbolContext;

public final class CodeDataVisitor implements Visitor {
    private final Generator generator;
    private final SymbolContext symbolContext;

    public CodeDataVisitor(SymbolContext symbolContext) {
        this.generator = Generator.getInstance();
        this.symbolContext = symbolContext;
    }

    @Override
    public void visit(Program n) {
        // before codegen visitor pass, need to:
        // a) initialize virtual tables
        // b) assign locations to instance variables
        symbolContext.buildVirtualTables();
        symbolContext.assignVariableOffsets();

        n.m.accept(this);

        generator.genDataSection();
        n.cl.forEach(c -> c.accept(this));
        generator.newLine();
    }

    @Override
    public void visit(MainClass n) {
        symbolContext.enterClass(n.i1.s);
        symbolContext.enterMethod("main");

        var main = symbolContext.getCurrentMethod();
        if (main == null) {
            throw new IllegalStateException();
        }

        // calculate main method stack frame size:
        // size of stack frame = local variable count
        int frameSize = main.localVariableCount();
        if (frameSize % 2 == 1) frameSize++;  // alignment
        main.frameSize = frameSize * Generator.WORD_SIZE;

        int offset = 1;
        // assign offsets to local variables
        for (int i = 0; i < main.localVariableCount(); i++) {
            var v = symbolContext.lookupVariable(main.getLocalVariable(i).name);
            if (v == null) {
                throw new IllegalStateException();
            }

            v.vIndex = -offset++;
        }

        symbolContext.exit();
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genLabel(n.i.s + "$$");
        generator.genUnary(".quad", "0");  // no superclass
        class_.methodEntries().forEachRemaining(m ->
                generator.genUnary(".quad", m.getQualifiedName()));

        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genLabel(n.i.s + "$$");
        generator.genUnary(".quad", n.j.s + "$$");  // superclass
        class_.methodEntries().forEachRemaining(m ->
                generator.genUnary(".quad", m.getQualifiedName()));

        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(VarDecl n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(VarInit n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(MethodDecl n) {
        var method = symbolContext.lookupMethod(n.i.s);
        if (method == null) {
            throw new IllegalStateException();
        }

        // calculate method stack frame size:
        // size of stack frame = arg count + local variable count + obj ptr
        int frameSize = method.argumentCount() + method.localVariableCount() + 1;
        if (frameSize % 2 == 1) frameSize++;  // alignment
        method.frameSize = frameSize * Generator.WORD_SIZE;

        int offset = 2;  // start offset at 2 to leave space for obj ptr

        symbolContext.enterMethod(n.i.s);
        // assign offsets to parameters
        for (int i = 0; i < n.fl.size(); i++) {
            var p = symbolContext.lookupVariable(n.fl.get(i).i.s);
            if (p == null) {
                throw new IllegalStateException();
            }

            p.vIndex = -offset++;
        }

        // assign offsets to local variables
        for (int i = 0; i < method.localVariableCount(); i++) {
            var v = symbolContext.lookupVariable(method.getLocalVariable(i).name);
            if (v == null) {
                throw new IllegalStateException();
            }

            v.vIndex = -offset++;
        }

        symbolContext.exit();
    }

    @Override
    public void visit(Formal n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(VoidType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayType n) {
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
        throw new IllegalStateException();
    }

    @Override
    public void visit(Return n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(If n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IfElse n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Switch n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(CaseSimple n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(CaseDefault n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(While n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(For n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Print n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignSimple n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignPlus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignMinus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignTimes n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignDivide n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignMod n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignAnd n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignOr n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignXor n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignLeftShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignRightShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(PostIncrement n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(PreIncrement n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(PostDecrement n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(PreDecrement n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(And n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Or n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Equal n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NotEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LessThan n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LessThanOrEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(GreaterThan n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(GreaterThanOrEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseAnd n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseOr n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseXor n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(UnaryMinus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(UnaryPlus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Plus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Minus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Times n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Divide n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Mod n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LeftShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(RightShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(UnsignedRightShift n) {
        throw new IllegalStateException();
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
    public void visit(Action n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Call n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Field n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Ternary n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(InstanceOf n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntegerLiteral n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(True n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(False n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IdentifierExp n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(This n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NewArray n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NewObject n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Not n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseNot n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Identifier n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NoOp n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NoOpExp n) {
        throw new IllegalStateException();
    }
}
