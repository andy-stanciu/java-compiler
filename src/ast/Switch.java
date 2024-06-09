package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Switch extends Statement {
    public Exp e;
    public CaseList cl;

    public Switch(Exp ae, CaseList acl, Location pos) {
        super(pos);
        e = ae;
        cl = acl;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
