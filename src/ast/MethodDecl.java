package ast;

import ast.visitor.Visitor;
import dataflow.DataflowGraph;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class MethodDecl extends ASTNode {
    public Type t;
    public Identifier i;
    public FormalList fl;
    public StatementList sl;
    public boolean conflict;
    public semantics.type.Type type;
    public DataflowGraph dataflow;
    public Location endPos;

    public MethodDecl(Type at, Identifier ai, FormalList afl,
                      StatementList asl, Location pos, Location endPos) {
        super(pos);
        t = at;
        i = ai;
        fl = afl;
        sl = asl;
        this.endPos = endPos;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
