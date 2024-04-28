package ast.Visitor.util;

import ast.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public final class PrecedentTracker {
    private static final Map<Class<? extends Exp>, Integer> precedenceTable = new HashMap<>();

    private final Stack<Exp> exps;

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
