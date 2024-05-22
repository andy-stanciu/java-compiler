package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Type extends ASTNode {
    public semantics.type.Type type;

    public Type(Location pos) {
        super(pos);
    }
    public abstract void accept(Visitor v);
}
