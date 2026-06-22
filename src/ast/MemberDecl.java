package ast;

import commons.Visitor;
import dataflow.DataflowGraph;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class MemberDecl extends ASTNode {
    public Identifier i;
    public FormalList fl;
    public StatementList sl;
    public boolean conflict;
    public DataflowGraph dataflow;
    public Location endPos;

    public MemberDecl(Identifier i, FormalList fl, StatementList sl, Location pos, Location endPos) {
        super(pos);
        this.i = i;
        this.fl = fl;
        this.sl = sl;
        this.endPos = endPos;
    }

    public abstract void accept(Visitor visitor);
}
