package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class Switch extends Statement {
    public Expression e;
    public CaseList cl;

    public Switch(Expression ae, CaseList acl, Location pos) {
        super(pos);
        e = ae;
        cl = acl;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
