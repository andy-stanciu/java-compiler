package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Minus extends BinaryExp {
    public Minus(Expression ae1, Expression ae2, Location pos) {
        super(ae1, ae2, pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
