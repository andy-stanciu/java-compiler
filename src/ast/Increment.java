package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Increment extends StatementSimple {
    public Assignable a;

    public Increment(Assignable ai, Location pos) {
        super(pos);
        a = ai;
    }
}
