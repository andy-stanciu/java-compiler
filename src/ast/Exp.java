package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.type.Type;

public abstract class Exp extends ASTNode implements Returnable {
    public Type type;

    public Exp(Location pos) {
        super(pos);
    }

    @Override
    public Type getReturnableType() {
        return type;
    }

    @Override
    public int getLineNumber() {
        return line_number;
    }

    public abstract void accept(Visitor v);
}
