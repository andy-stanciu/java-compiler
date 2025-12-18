package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class BitwiseXor extends BinaryExp {
    public BitwiseXor(Expression ae1, Expression ae2, Location pos) {
        super(ae1, ae2, pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
