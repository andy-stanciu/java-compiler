package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class ArrayLookup extends Exp implements Assignable {
    public Expression e1;
    public ExpressionList el;

    public ArrayLookup(Expression ae1, ExpressionList ael, Location pos) {
        super(pos);
        e1 = ae1;
        el = ael;
    }

    public void addDimension(Expression e) {
        el.add(e);
    }

    public int getDimensionCount() {
        return el.size();
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
