package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class UnaryMinus extends Exp {
    public Expression e;

    public UnaryMinus(Expression ae, Location pos) {
        super(pos);
        e = ae;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
