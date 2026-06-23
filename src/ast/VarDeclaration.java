package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.type.Type;

public abstract class VarDeclaration extends StatementSimple {
    public ast.Type t;
    public Identifier i;
    public Type type;

    public VarDeclaration(ast.Type at, Identifier ai, Location pos) {
        super(pos);
        t = at;
        i = ai;
    }
}