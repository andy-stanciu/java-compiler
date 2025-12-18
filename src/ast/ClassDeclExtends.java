package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class ClassDeclExtends extends ClassDecl {
    public Identifier i;
    public Identifier j;
    public DeclarationList dl;
    public MethodDeclList ml;

    public ClassDeclExtends(Identifier ai, Identifier aj,
                            DeclarationList adl, MethodDeclList aml,
                            Location pos) {
        super(pos);
        i = ai;
        j = aj;
        dl = adl;
        ml = aml;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
