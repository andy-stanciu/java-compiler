package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Case extends ASTNode {
    public StatementList sl;
    public boolean breaks;

    public Case(StatementList asl, boolean breaks, Location pos) {
        super(pos);
        sl = asl;
        this.breaks = breaks;
    }

    public abstract void accept(Visitor v);
}
