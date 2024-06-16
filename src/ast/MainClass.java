package ast;

import ast.visitor.Visitor;
import dataflow.DataflowGraph;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class MainClass extends ASTNode {
    public Identifier i1, i2;
    public StatementList sl;
    public DataflowGraph dataflow;

    public MainClass(Identifier ai1, Identifier ai2, StatementList asl,
                     Location pos) {
        super(pos);
        i1 = ai1;
        i2 = ai2;
        sl = asl;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}

