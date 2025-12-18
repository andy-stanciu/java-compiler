package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class Ternary extends Exp {
    public Expression c, e1, e2;

    public Ternary(Expression ac, Expression ae1,
                   Expression ae2, Location pos) {
        super(pos);
        c = ac;
        e1 = ae1;
        e2 = ae2;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
