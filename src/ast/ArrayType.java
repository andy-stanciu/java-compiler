package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class ArrayType extends DeclarableType {
    public SingularType t;
    public int dimension;

    public ArrayType(SingularType at, Location pos) {
        super(pos);
        t = at;
        dimension = 1;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
