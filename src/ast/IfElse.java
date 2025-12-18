package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class IfElse extends Statement {
    public Expression e;
    public Statement s1, s2;

    public IfElse(Expression ae, Statement as1, Statement as2, Location pos) {
        super(pos);
        e = ae;
        s1 = as1;
        s2 = as2;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}

