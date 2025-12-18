package ast;

import commons.Visitor;

public interface Assignable extends Expression {
    void accept(Visitor visitor);
}
