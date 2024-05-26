package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Increment extends Statement {
    public Assignable a;

    public Increment(Assignable ai, Location pos) {
        super(pos);
        a = ai;
    }

    public abstract void accept(Visitor v);
}
