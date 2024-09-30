package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Call extends Exp {
    public Expression e;
    public Identifier i;
    public ExpressionList el;

    public Call(Expression ae, Identifier ai, ExpressionList ael, Location pos) {
        super(pos);
        e = ae;
        i = ai;
        el = ael;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
