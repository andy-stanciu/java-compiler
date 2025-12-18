package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class While extends Statement {
    public Expression e;
    public Statement s;

    public While(Expression ae, Statement as, Location pos) {
        super(pos);
        e = ae;
        s = as;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}

