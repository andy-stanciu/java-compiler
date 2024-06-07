package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class StatementSimple extends Statement {
    public StatementSimple(Location pos) {
        super(pos);
    }
}
