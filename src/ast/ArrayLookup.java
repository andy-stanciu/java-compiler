package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;
import semantics.type.Type;
import semantics.type.TypeArray;
import semantics.type.TypeSingular;

public class ArrayLookup extends Exp implements Assignable {
    public Exp e1;
    public ExpList el;

    public ArrayLookup(Exp ae1, ExpList ael, Location pos) {
        super(pos);
        e1 = ae1;
        el = ael;
    }

    public void addDimension(Exp e) {
        el.add(e);
    }

    public int getDimensionCount() {
        return el.size();
    }

    @Override
    public Type getAssignableType() {
        return type;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
