package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.type.TypeInt;

public abstract class Increment extends StatementSimple implements Expression {
    private static final Exp rvalue = new IntegerLiteral(1, new Location(0, 0));
    public Expression e;

    static {
        rvalue.type = TypeInt.getInstance();
    }

    public Increment(Expression ei, Location pos) {
        super(pos);
        e = ei;
    }

    @Override
    public Exp eval() {
        return rvalue;
    }
}
