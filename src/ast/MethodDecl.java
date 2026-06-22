package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;
import semantics.type.Type;

public class MethodDecl extends MemberDecl {
    public ast.Type t;
    public Type type; // return type

    public MethodDecl(ast.Type at, Identifier ai, FormalList afl,
                      StatementList asl, Location pos, Location endPos) {
        super(ai, afl, asl, pos, endPos);
        t = at;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
