package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class MethodDecl extends ASTNode {
    public Type t;
    public Identifier i;
    public FormalList fl;
    public VarDeclList vl;
    public StatementList sl;
    public Exp e;
    public boolean conflict;
    public semantics.type.Type type;

    public MethodDecl(Type at, Identifier ai, FormalList afl, VarDeclList avl,
                      StatementList asl, Exp ae, Location pos) {
        super(pos);
        t = at;
        i = ai;
        fl = afl;
        vl = avl;
        sl = asl;
        e = ae;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
