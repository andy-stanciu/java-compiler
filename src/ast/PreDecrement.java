package ast;

import ast.visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.Logger;

public class PreDecrement extends Increment {
    public PreDecrement(Expression ei, Location pos) {
        super(ei, pos);
    }

    @Override
    public void accept(Visitor v) {
        Logger.getInstance().setLineNumber(line_number);
        v.visit(this);
    }
}
