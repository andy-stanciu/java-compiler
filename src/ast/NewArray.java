package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class NewArray extends Exp {
    public ExpressionList el;
    public SingularType t;

    public NewArray(SingularType at, ExpressionList ael, Location pos) {
        super(pos);
        el = ael;
        t = at;
    }

    public void addDimension(Expression e) {
        el.add(e);
    }

    public Expression getDimension(int i) {
        return el.get(i);
    }

    public int getDimensionCount() {
        return el.size();
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
