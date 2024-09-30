package ast;

import ast.visitor.Visitor;

public interface Expression {
    Exp eval();

    void accept(Visitor visitor);
}
