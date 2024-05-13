package ast;

import ast.visitor.Visitor;
import semantics.type.Type;

public interface Assignable {
    Type getAssignableType();

    void accept(Visitor visitor);
}
