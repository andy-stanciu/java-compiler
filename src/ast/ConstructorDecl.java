package ast;

import commons.Logger;
import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.info.Signature;

public class ConstructorDecl extends MemberDecl {
    public Signature signature;

    public ConstructorDecl(Identifier cid, FormalList afl, StatementList asl, Location pos, Location endPos) {
        super(cid, afl, asl, pos, endPos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
