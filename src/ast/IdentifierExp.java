package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class IdentifierExp extends Exp {
    public String s;

    public IdentifierExp(String as, Location pos) {
        super(pos);
        s = as;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
