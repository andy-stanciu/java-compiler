package ast;

import commons.Logger;
import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class NullLiteral extends Exp {
    public NullLiteral(Location pos) {
        super(pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
