package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Assign extends Statement {
    public Identifier i;
    public Exp e;

    public Assign(Identifier ai, Exp ae, Location pos) {
        super(pos);
        i = ai;
        e = ae;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}

