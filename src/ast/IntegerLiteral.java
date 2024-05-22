package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class IntegerLiteral extends Exp {
    public int i;

    public IntegerLiteral(int ai, Location pos) {
        super(pos);
        i = ai;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
