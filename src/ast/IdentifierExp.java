package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class IdentifierExp extends Exp implements Assignable {
    public String s;

    public IdentifierExp(String as, Location pos) {
        super(pos);
        s = as;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
