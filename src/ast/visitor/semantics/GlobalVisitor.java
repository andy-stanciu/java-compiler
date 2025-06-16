package ast.visitor.semantics;

import ast.*;
import ast.visitor.LazyVisitor;
import semantics.table.SymbolContext;

/**
 * Validates all class declarations. This consists of:
 * 1) Defines all class identifiers.
 * 2) Defines main method.
 */
public final class GlobalVisitor extends LazyVisitor {
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
}
