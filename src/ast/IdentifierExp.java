package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;
import semantics.type.Type;

public class IdentifierExp extends Exp implements Assignable {
    public String s;

    public IdentifierExp(String as, Location pos) {
        super(pos);
        s = as;
    }

    @Override
    public Type getAssignableType() {
        return type;
    }

    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
