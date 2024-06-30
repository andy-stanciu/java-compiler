package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class If extends Statement {
    public Expression e;
    public Statement s;

    public If(Expression ae, Statement as, Location pos) {
        super(pos);
        e = ae;
        s = as;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
