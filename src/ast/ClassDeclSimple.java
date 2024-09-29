package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

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
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
