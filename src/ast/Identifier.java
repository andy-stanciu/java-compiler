package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class Identifier extends ASTNode {
    public String s;

    public Identifier(String as, Location pos) {
        super(pos);
        s = as;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }

    public String toString() {
        return s;
    }
}
