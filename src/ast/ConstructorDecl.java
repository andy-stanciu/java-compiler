package ast;

import commons.Logger;
import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.info.ConstructorInfo;

public class ConstructorDecl extends MemberDecl {
    // associated constructor info
    public ConstructorInfo constructorInfo;

    public ConstructorDecl(Identifier cid, FormalList afl, StatementList asl, Location pos, Location endPos) {
        super(cid, afl, asl, pos, endPos);
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
