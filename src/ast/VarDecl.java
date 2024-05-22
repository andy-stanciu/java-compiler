package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class VarDecl extends ASTNode {
    public Type t;
    public Identifier i;
    public boolean conflict;
    public semantics.type.Type type;

    public VarDecl(Type at, Identifier ai, Location pos) {
        super(pos);
        t = at;
        i = ai;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
