package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class MainClass extends ASTNode {
    public Identifier i1, i2;
    public Statement s;

    public MainClass(Identifier ai1, Identifier ai2, Statement as,
                     Location pos) {
        super(pos);
        i1 = ai1;
        i2 = ai2;
        s = as;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}

