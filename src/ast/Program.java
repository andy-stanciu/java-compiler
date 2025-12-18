package ast;

import commons.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import commons.Logger;

public class Program extends ASTNode {
    public MainClass m;
    public ClassDeclList cl;

    public Program(MainClass am, ClassDeclList acl, Location pos) {
        super(pos);
        m = am;
        cl = acl;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(lineNumber);
        v.visit(this);
    }
}
