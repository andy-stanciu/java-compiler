package AST.Visitor.util;

import AST.*;

import java.util.HashMap;
import java.util.Map;

public final class PrecedenceUtil {
    private static final Map<Class<? extends Exp>, Integer> precedenceTable = new HashMap<>();

    static {
        precedenceTable.put(And.class, 1);              // &&
        precedenceTable.put(LessThan.class, 2);         // <
        precedenceTable.put(Plus.class, 3);             // +
        precedenceTable.put(Minus.class, 3);            // -
        precedenceTable.put(Times.class, 4);            // *
        precedenceTable.put(NewArray.class, 5);         // new
        precedenceTable.put(NewObject.class, 5);        // new
        precedenceTable.put(Not.class, 6);              // !
        precedenceTable.put(ArrayLength.class, 10);     // .
        precedenceTable.put(ArrayLookup.class, 10);     // [
        precedenceTable.put(Call.class, 10);            // .
        precedenceTable.put(True.class, 10);            // true
        precedenceTable.put(False.class, 10);           // false
        precedenceTable.put(IdentifierExp.class, 10);   // identifier
        precedenceTable.put(IntegerLiteral.class, 10);  // int literal
        precedenceTable.put(This.class, 10);            // this
    }

    public static PrecedenceUtil create() {
        return new PrecedenceUtil();
    }

    private PrecedenceUtil() {}

    public boolean should
}
