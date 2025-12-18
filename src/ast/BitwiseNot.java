package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class BitwiseNot extends Exp {
    public Expression e;

    public BitwiseNot(Expression ae, Location pos) {
        super(pos);
        e = ae;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
