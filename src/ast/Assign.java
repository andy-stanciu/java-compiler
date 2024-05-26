package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Assign extends Statement {
    public Assignable a;
    public Exp e;

    public Assign(Assignable ai, Exp ae, Location pos) {
        super(pos);
        a = ai;
        e = ae;
    }

    public abstract void accept(Visitor v);
}
