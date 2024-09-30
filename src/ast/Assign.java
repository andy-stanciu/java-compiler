package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Assign extends StatementSimple implements Expression {
    public Expression e1;
    public Expression e2;

    public Assign(Expression ei, Expression ae, Location pos) {
        super(pos);
        e1 = ei;
        e2 = ae;
    }

    @Override
    public Exp eval() {
        return e2.eval();
    }
}
