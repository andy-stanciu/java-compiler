package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class AssignTimes extends Assign {
    public AssignTimes(Expression ei, Expression ae, Location pos) {
        super(ei, ae, pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
