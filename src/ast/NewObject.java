package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class NewObject extends Exp {
    public Identifier i;

    public NewObject(Identifier ai, Location pos) {
        super(pos);
        i = ai;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
