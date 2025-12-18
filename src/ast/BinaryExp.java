package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class BinaryExp extends Exp {
    public Expression e1, e2;

    public BinaryExp(Expression ae1, Expression ae2, Location pos) {
        super(pos);
        e1 = ae1;
        e2 = ae2;
    }

    public abstract void accept(Visitor v);
}
