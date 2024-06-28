package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.type.Type;

public abstract class Exp extends ASTNode implements Expression {
    public Type type;

    public Exp(Location pos) {
        super(pos);
    }

    public abstract void accept(Visitor v);

    @Override
    public Exp getExp() {
        return this;
    }
}
