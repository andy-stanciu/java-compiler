package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Assign extends Statement {
    public Identifier i;
    public Exp e;

    public Assign(Identifier ai, Exp ae, Location pos) {
        super(pos);
        i = ai;
        e = ae;
    }

    public abstract void accept(Visitor v);
}
