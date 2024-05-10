package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class BinaryExp extends Exp {
    public Exp e1, e2;

    public BinaryExp(Exp ae1, Exp ae2, Location pos) {
        super(pos);
        e1 = ae1;
        e2 = ae2;
    }

    public abstract void accept(Visitor v);
}
