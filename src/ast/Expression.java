package ast;

import ast.visitor.Visitor;

public interface Expression {
    Exp getExp();

    void accept(Visitor visitor);
}
