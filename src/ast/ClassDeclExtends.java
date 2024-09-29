package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

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
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
