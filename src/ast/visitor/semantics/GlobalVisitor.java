package ast.visitor.semantics;

import ast.*;
import ast.visitor.Visitor;
import semantics.table.SymbolContext;

/**
 * Validates all class declarations. This consists of:
 * 1) Defines all class identifiers.
 * 2) Defines main method.
 */
public final class GlobalVisitor implements Visitor {
    private final SymbolContext symbolContext;

    public GlobalVisitor(SymbolContext symbolContext) {
        this.symbolContext = symbolContext;
    }

    @Override
    public void visit(Program n) {
        n.m.accept(this);  // always visit the main class first
        n.cl.forEach(c -> c.accept(this));
    }

    @Override
    public void visit(MainClass n) {
        // name conflict should be impossible for main class
        symbolContext.addClassEntry(n.i1.s, true);
    }

    @Override
    public void visit(ClassDeclSimple n) {
        n.conflict = symbolContext.addClassEntry(n.i.s) == null;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        n.conflict = symbolContext.addClassEntry(n.i.s) == null;
    }

    @Override
    public void visit(VarDecl n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(MethodDecl n) {
        throw new IllegalStateException();
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
