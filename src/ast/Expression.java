package ast;

import commons.Visitor;

public interface Expression {
    Exp eval();

    void accept(Visitor visitor);
}
