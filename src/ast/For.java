package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class For extends Statement {
    public StatementSimple s0, s1;
    public Exp e;
    public Statement s2;

    public For(StatementSimple as0, Exp ae,
               StatementSimple as1, Statement as2, Location pos) {
        super(pos);
        s0 = as0;
        s1 = as1;
        e = ae;
        s2 = as2;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
