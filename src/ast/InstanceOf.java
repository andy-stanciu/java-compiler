package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class InstanceOf extends Exp {
    public Exp e;
    public Identifier i;

    public InstanceOf(Exp ae, Identifier ai, Location pos) {
        super(pos);
        e = ae;
        i = ai;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
