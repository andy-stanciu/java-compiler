package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Case extends ASTNode {
    public int n;
    public StatementList sl;
    public boolean breaks;

    public Case(int an, StatementList asl, boolean breaks, Location pos) {
        super(pos);
        n = an;
        sl = asl;
        this.breaks = breaks;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
