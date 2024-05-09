package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory;
import semantics.Logger;

public class Ternary extends Exp {
    public Exp c, e1, e2;

    public And(Exp ac, Exp ae1, Exp ae2, ComplexSymbolFactory.Location pos) {
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
