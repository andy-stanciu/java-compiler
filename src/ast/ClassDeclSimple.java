package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class ClassDeclSimple extends ClassDecl {
    public Identifier i;
    public DeclarationList dl;
    public MethodDeclList ml;

    public ClassDeclSimple(Identifier ai, DeclarationList adl, MethodDeclList aml,
                           Location pos) {
        super(pos);
        i = ai;
        dl = adl;
        ml = aml;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
