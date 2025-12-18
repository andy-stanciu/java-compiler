package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;
import semantics.info.BlockInfo;

public class For extends Statement {
    public StatementSimple s0, s1;
    public Expression e;
    public Statement s2;
    public BlockInfo blockInfo; // for loop has its own block

    public For(StatementSimple as0, Expression ae,
               StatementSimple as1, Statement as2, Location pos) {
        super(pos);
        s0 = as0;
        s1 = as1;
        e = ae;
        s2 = as2;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
