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

        generator.genDataSection();
        n.cl.forEach(c -> c.accept(this));
        generator.newLine();
    }

    @Override
    public void visit(MainClass n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genLabel(n.i.s + "$$");
        generator.genUnary(".quad", "0");  // no superclass
        class_.methodEntries().forEachRemaining(m -> {
            generator.genUnary(".quad", m.getQualifiedName());
        });

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
        class_.methodEntries().forEachRemaining(m -> {
            generator.genUnary(".quad", m.getQualifiedName());
        });

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

        // calculate method stack frame size:
        // size of stack frame = arg count + local variable count + obj ptr
        int frameSize = n.fl.size() + n.vl.size() + 1;
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

            p.vIndex = -offset;
            offset++;
        }

        // assign offsets to local variables
        for (int i = 0; i < n.vl.size(); i++) {
            var v = symbolContext.lookupVariable(n.vl.get(i).i.s);
            if (v == null) {
                throw new IllegalStateException();
            }

            v.vIndex = -offset;
            offset++;
        }

        symbolContext.exit();
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
        throw new IllegalStateException();
    }

    @Override
    public void visit(If n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(While n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Print n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Assign n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayAssign n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(And n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LessThan n) {
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
    public void visit(ArrayLookup n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayLength n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Call n) {
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
    public void visit(Identifier n) {
        throw new IllegalStateException();
    }
}
