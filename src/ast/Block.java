package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Block extends Statement {
    public StatementList sl;

    public Block(StatementList asl, Location pos) {
        super(pos);
        sl = asl;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}

