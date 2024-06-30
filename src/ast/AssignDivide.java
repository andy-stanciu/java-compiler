package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class AssignDivide extends Assign {
    public AssignDivide(Assignable ai, Expression ae, Location pos) {
        super(ai, ae, pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
