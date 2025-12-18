package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class ArrayType extends DeclarableType {
    public SingularType t;
    public int dimension;

    public ArrayType(SingularType at, Location pos) {
        super(pos);
        t = at;
        dimension = 1;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
