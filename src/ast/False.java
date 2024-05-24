package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class False extends Exp {
    public False(Location pos) {
        super(pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
