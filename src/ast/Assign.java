package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Assign extends StatementSimple implements Expression {
    public Assignable a;
    public Expression e;

    public Assign(Assignable ai, Expression ae, Location pos) {
        super(pos);
        a = ai;
        e = ae;
    }

    @Override
    public Exp eval() {
        return a.eval();
    }
}
