package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Default extends ASTNode {
    public StatementList sl;

    public Default(StatementList asl, Location pos) {
        super(pos);
        sl = asl;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
