package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class PreIncrement extends Increment {
    public PreIncrement(Assignable ai, Location pos) {
        super(ai, pos);
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
