package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Increment extends StatementSimple implements Expression {
    public Assignable a;

    public Increment(Assignable ai, Location pos) {
        super(pos);
        a = ai;
    }

    @Override
    public Exp eval() {
        return a.eval();
    }
}
