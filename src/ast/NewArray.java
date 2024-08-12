package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class NewArray extends Exp {
    public ExpList el;
    public SingularType t;

    public NewArray(SingularType at, ExpList ael, Location pos) {
        super(pos);
        el = ael;
        t = at;
    }

    public void addDimension(Exp e) {
        el.add(e);
    }

    public Exp getDimension(int i) {
        return el.get(i);
    }

    public int getDimensionCount() {
        return el.size();
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
