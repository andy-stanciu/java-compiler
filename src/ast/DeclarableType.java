package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class DeclarableType extends Type {
    public DeclarableType(Location pos) {
        super(pos);
    }
}
