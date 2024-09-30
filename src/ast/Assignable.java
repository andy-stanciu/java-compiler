package ast;

import ast.visitor.Visitor;

public interface Assignable extends Expression {
    void accept(Visitor visitor);
}
