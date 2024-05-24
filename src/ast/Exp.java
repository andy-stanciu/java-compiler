package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.type.Type;

public abstract class Exp extends ASTNode {
    public Type type;

    public Exp(Location pos) {
        super(pos);
    }
    public abstract void accept(Visitor v);
}
