package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class InstanceOf extends Exp {
    public Expression e;
    public Identifier i;

    public InstanceOf(Expression ae, Identifier ai, Location pos) {
        super(pos);
        e = ae;
        i = ai;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
