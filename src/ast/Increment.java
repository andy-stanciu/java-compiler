package ast;

import java_cup.runtime.ComplexSymbolFactory.Location;
import semantics.type.TypeInt;

public abstract class Increment extends StatementSimple implements Expression {
    private static final Exp rvalue = new IntegerLiteral(1, new Location(0, 0));
    public Assignable a;

    static {
        rvalue.type = TypeInt.getInstance();
    }

    public Increment(Assignable ai, Location pos) {
        super(pos);
        a = ai;
    }

    @Override
    public Exp eval() {
        return rvalue;
    }
}
