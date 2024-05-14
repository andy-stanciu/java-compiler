package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Identifier extends ASTNode {
    public String s;

    public Identifier(String as, Location pos) {
        super(pos);
        s = as;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }

    public String toString() {
        return s;
    }
}
