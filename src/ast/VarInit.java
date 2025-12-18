package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class VarInit extends Declaration {
    public Expression e;

    public VarInit(Type at, Identifier ai, Expression ae, Location pos) {
        super(at, ai, pos);
        e = ae;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
