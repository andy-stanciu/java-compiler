package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class Call extends Exp {
    public Expression e;
    public Identifier i;
    public ExpressionList el;

    public Call(Expression ae, Identifier ai, ExpressionList ael, Location pos) {
        super(pos);
        e = ae;
        i = ai;
        el = ael;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
