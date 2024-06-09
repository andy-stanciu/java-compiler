package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class CaseDefault extends Case {
    public CaseDefault(StatementList asl, boolean breaks, Location pos) {
        super(asl, breaks, pos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
