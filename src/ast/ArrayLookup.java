package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class ArrayLookup extends Exp implements Assignable {
    public Exp e1, e2;

    public ArrayLookup(Exp ae1, Exp ae2, Location pos) {
        super(pos);
        e1 = ae1;
        e2 = ae2;
    }

    @Override
    public Exp getExp() {
        return e1;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
