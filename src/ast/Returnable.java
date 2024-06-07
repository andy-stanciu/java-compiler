package ast;

import ast.visitor.Visitor;
import semantics.type.Type;

public interface Returnable {
    Type getReturnableType();

    int getLineNumber();

    void accept(Visitor v);
}
