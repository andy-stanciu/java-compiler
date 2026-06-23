package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;
import semantics.info.ConstructorInfo;

public class NewObject extends Exp {
    public Identifier i;
    public ExpressionList el;
    public ConstructorInfo resolvedConstructor;

    public NewObject(Identifier ai, ExpressionList ael, Location pos) {
        super(pos);
        el = ael;
        i = ai;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
