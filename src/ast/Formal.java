package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;
import semantics.type.Type;

public class Formal extends ASTNode {
    public ast.Type t;
    public Identifier i;
    public Type type;

    public Formal(ast.Type at, Identifier ai, Location pos) {
        super(pos);
        t = at;
        i = ai;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
