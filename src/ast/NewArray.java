package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class NewArray extends Exp {
    public Exp e;

    public NewArray(Exp ae, Location pos) {
        super(pos);
        e = ae;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
