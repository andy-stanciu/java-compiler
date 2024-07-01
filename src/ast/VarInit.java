package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class VarInit extends Declaration {
    public Exp e;

    public VarInit(Type at, Identifier ai, Exp ae, Location pos) {
        super(at, ai, pos);
        e = ae;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
