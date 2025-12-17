package ast.visitor.dataflow;

import ast.*;
import ast.visitor.LazyVisitor;
import dataflow.Symbol;

import java.util.Set;

/**
 * Called when constructing the dataflow graph to populate def and use sets for all statements.
 * Some statements cannot exist as instructions, and thus will throw if this visitor attempts
 * to visit them.
 */
public class LiveVariableVisitor extends LazyVisitor {
    private Set<Symbol> used;

    @Override
    public void visit(VarDecl n) {
        n.defined.add(new Symbol(n.i.s, n.i.line_number));
    }

    @Override
    public void visit(VarInit n) {
        n.defined.add(new Symbol(n.i.s, n.i.line_number));
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(Return n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(If n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(IfElse n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(Switch n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(While n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(For n) {
        used = n.used;
        n.s0.accept(this);
        n.defined.addAll(n.s0.defined);
    }

    @Override
    public void visit(Print n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(AssignSimple n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignPlus n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignMinus n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignTimes n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignDivide n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignMod n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignAnd n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignOr n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignXor n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignLeftShift n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignRightShift n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        used = n.used;
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(PostIncrement n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(PreIncrement n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(PostDecrement n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(PreDecrement n) {
        used = n.used;
        n.e.accept(this);
    }

    @Override
    public void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(Or n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(Equal n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(NotEqual n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(LessThan n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(LessThanOrEqual n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(GreaterThan n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(GreaterThanOrEqual n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(BitwiseAnd n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(BitwiseOr n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(BitwiseXor n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(UnaryMinus n) {
        n.e.accept(this);
    }

    @Override
    public void visit(UnaryPlus n) {
        n.e.accept(this);
    }

    @Override
    public void visit(Plus n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(Divide n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(Mod n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(LeftShift n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(RightShift n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(UnsignedRightShift n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        n.el.forEach(e -> e.accept(this));
    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);
    }

    @Override
    public void visit(Action n) {
        used = n.used;
        n.c.accept(this);
    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);
        n.el.forEach(e -> e.accept(this));
    }

    @Override
    public void visit(Field n) {
        // fields / instance variables should be omitted
    }

    @Override
    public void visit(Ternary n) {
        n.c.accept(this);
        n.e1.accept(this);
        n.e2.accept(this);
    }

    @Override
    public void visit(InstanceOf n) {
        n.e.accept(this);
    }

    @Override
    public void visit(IntegerLiteral n) {}

    @Override
    public void visit(StringLiteral n) {}

    @Override
    public void visit(True n) {}

    @Override
    public void visit(False n) {}

    @Override
    public void visit(IdentifierExp n) {
        if (used == null) {
            throw new IllegalStateException();
        }

        used.add(new Symbol(n.s, n.line_number));
    }

    @Override
    public void visit(This n) {}

    @Override
    public void visit(NewArray n) {
        n.el.forEach(e -> e.accept(this));
    }

    @Override
    public void visit(NewObject n) {}

    @Override
    public void visit(Not n) {
        n.e.accept(this);
    }

    @Override
    public void visit(BitwiseNot n) {
        n.e.accept(this);
    }

    @Override
    public void visit(NoOp n) {}

    @Override
    public void visit(NoOpExp n) {}
}
