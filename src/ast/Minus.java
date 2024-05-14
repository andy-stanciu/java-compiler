package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Minus extends Exp {
    public Exp e1, e2;

    public Minus(Exp ae1, Exp ae2, Location pos) {
        super(pos);
        e1 = ae1;
        e2 = ae2;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
