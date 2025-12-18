package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class IntegerLiteral extends Exp {
    public int i;

    public IntegerLiteral(int ai, Location pos) {
        super(pos);
        i = ai;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
