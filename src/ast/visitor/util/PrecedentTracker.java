package ast.visitor.util;

import ast.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public final class PrecedentTracker {
    private static final Map<Class<? extends Exp>, Integer> precedenceTable = new HashMap<>();

    private final Stack<Exp> exps;

    static {
        precedenceTable.put(Ternary.class, 20);             // ?:
        precedenceTable.put(Or.class, 30);                  // ||
        precedenceTable.put(And.class, 40);                 // &&
        precedenceTable.put(BitwiseOr.class, 50);           // |
        precedenceTable.put(BitwiseXor.class, 60);          // ^
        precedenceTable.put(BitwiseAnd.class, 70);          // &
        precedenceTable.put(Equal.class, 80);               // ==
        precedenceTable.put(NotEqual.class, 80);            // !=
        precedenceTable.put(LessThan.class, 90);            // <
        precedenceTable.put(LessThanOrEqual.class, 90);     // <=
        precedenceTable.put(GreaterThan.class, 90);         // >
        precedenceTable.put(GreaterThanOrEqual.class, 90);  // >=
        precedenceTable.put(InstanceOf.class, 90);          // instanceof
        precedenceTable.put(LeftShift.class, 95);           // <<
        precedenceTable.put(RightShift.class, 95);          // >>
        precedenceTable.put(UnsignedRightShift.class, 95);  // >>>
        precedenceTable.put(Plus.class, 100);               // +
        precedenceTable.put(Minus.class, 100);              // -
        precedenceTable.put(Times.class, 110);              // *
        precedenceTable.put(Divide.class, 110);             // /
        precedenceTable.put(Mod.class, 110);                // %
        precedenceTable.put(Not.class, 120);                // !
        precedenceTable.put(BitwiseNot.class, 120);         // ~
        precedenceTable.put(NewArray.class, 200);           // new
        precedenceTable.put(NewObject.class, 200);          // new
        precedenceTable.put(ArrayLength.class, 500);        // .
        precedenceTable.put(ArrayLookup.class, 500);        // [
        precedenceTable.put(Call.class, 500);               // .
        precedenceTable.put(Field.class, 500);              // .
        precedenceTable.put(True.class, 500);               // true
        precedenceTable.put(False.class, 500);              // false
        precedenceTable.put(IdentifierExp.class, 500);      // identifier
        precedenceTable.put(IntegerLiteral.class, 500);     // int literal
        precedenceTable.put(This.class, 500);               // this
    }

    public static PrecedentTracker create() {
        return new PrecedentTracker();
    }

    private PrecedentTracker() {
        this.exps = new Stack<>();
    }

    public void push(Exp exp) {
        exps.push(exp);
    }

    public void pop() {
        exps.pop();
    }

    private boolean shouldParen(Exp exp) {
        if (exps.isEmpty()) {
            return false;
        }

        Exp last = exps.peek();
        if (last instanceof ArrayLookup ||  // these expressions are already bounded by () or []
            last instanceof Call ||
            last instanceof NewArray) {
            return false;
        }

        int expPrecedence = precedenceTable.get(exp.getClass());
        int lastPrecedence = precedenceTable.get(last.getClass());

        return expPrecedence < lastPrecedence;  // only parenthesize if strictly lower precedence
    }

    public void leftParen(Exp exp) {
        if (shouldParen(exp)) {
            System.out.print("(");
        }
    }

    public void rightParen(Exp exp) {
        if (shouldParen(exp)) {
            System.out.print(")");
        }
    }
}
