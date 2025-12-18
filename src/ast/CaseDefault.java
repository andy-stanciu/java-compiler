package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class CaseDefault extends Case {
    public CaseDefault(StatementList asl, boolean breaks, Location pos) {
        super(asl, breaks, pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
