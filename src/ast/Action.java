package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class Action extends StatementSimple {
    public Call c;

    public Action(Call ac, Location pos) {
        super(pos);
        c = ac;
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(c);
    }
}
