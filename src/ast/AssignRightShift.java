package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class AssignRightShift extends Assign {
    public AssignRightShift(Expression ei, Expression ae, Location pos) {
        super(ei, ae, pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
