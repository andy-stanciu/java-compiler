package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class SingularType extends DeclarableType {
    public SingularType(Location pos) {
        super(pos);
    }
}
