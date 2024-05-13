package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class AssignSimple extends Assign {
    public AssignSimple(Assignable ai, Exp ae, Location pos) {
        super(ai, ae, pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}

