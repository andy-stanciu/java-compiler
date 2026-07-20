package ast;

import commons.Logger;
import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;


public class ThisCtorInvocation extends Statement {
    public ExpressionList el;

    public ThisCtorInvocation(ExpressionList el, Location pos) {
        super(pos);
        this.el = el;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
