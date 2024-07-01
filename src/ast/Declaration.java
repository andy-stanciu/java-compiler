package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Declaration extends StatementSimple {
    public Type t;
    public Identifier i;
    public semantics.type.Type type;

    public Declaration(Type at, Identifier ai, Location pos) {
        super(pos);
        t = at;
        i = ai;
    }
}