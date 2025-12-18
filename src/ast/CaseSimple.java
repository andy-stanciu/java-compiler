package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class CaseSimple extends Case {
    public int n;

    public CaseSimple(int an, StatementList asl, boolean breaks, Location pos) {
        super(asl, breaks, pos);
        n = an;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
