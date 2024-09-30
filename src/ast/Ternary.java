package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

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
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
