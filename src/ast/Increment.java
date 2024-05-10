package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Increment extends Statement {
    public Identifier i;

    public Increment(Identifier ai, Location pos) {
        super(pos);
        i = ai;
    }

    public abstract void accept(Visitor v);
}
