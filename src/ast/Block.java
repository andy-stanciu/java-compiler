package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;
import semantics.info.BlockInfo;

public class Block extends Statement {
    public StatementList sl;
    public BlockInfo blockInfo;

    public Block(StatementList asl, Location pos) {
        super(pos);
        sl = asl;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}

