package ast;

import commons.Visitor;
import dataflow.DataflowGraph;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;
import semantics.type.Type;

public class MethodDecl extends ASTNode {
    public ast.Type t;
    public Identifier i;
    public FormalList fl;
    public StatementList sl;
    public boolean conflict;
    public Type type;
    public DataflowGraph dataflow;
    public Location endPos;

    public MethodDecl(ast.Type at, Identifier ai, FormalList afl,
                      StatementList asl, Location pos, Location endPos) {
        super(pos);
        t = at;
        i = ai;
        fl = afl;
        sl = asl;
        this.endPos = endPos;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
