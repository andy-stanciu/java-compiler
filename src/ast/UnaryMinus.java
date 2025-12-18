package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class UnaryMinus extends Exp {
    public Expression e;

    public UnaryMinus(Expression ae, Location pos) {
        super(pos);
        e = ae;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
