package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Assign extends StatementSimple implements Expression {
    public Assignable a;
    public Exp e;

    public Assign(Assignable ai, Exp ae, Location pos) {
        super(pos);
        a = ai;
        e = ae;
    }

    @Override
    public Exp getExp() {
        return a.getExp();
    }
}
