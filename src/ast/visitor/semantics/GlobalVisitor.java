package ast.visitor.semantics;

import ast.*;
import ast.visitor.Visitor;
import semantics.table.SymbolContext;

/**
 * Validates all class declarations. This consists of:
 * 1) Defines all class identifiers.
 * 2) Defines main method.
 */
public final class GlobalVisitor implements Visitor {
    private final SymbolContext symbolContext;

    public GlobalVisitor(SymbolContext symbolContext) {
        this.symbolContext = symbolContext;
    }

    @Override
    public void visit(Program n) {
        n.m.accept(this);  // always visit the main class first
        n.cl.forEach(c -> c.accept(this));
    }

    @Override
    public void visit(MainClass n) {
        // name conflict should be impossible for main class
        symbolContext.addClassEntry(n.i1.s, true);
    }

    @Override
    public void visit(ClassDeclSimple n) {
        n.conflict = symbolContext.addClassEntry(n.i.s) == null;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        n.conflict = symbolContext.addClassEntry(n.i.s) == null;
    }

    @Override
    public void visit(VarDecl n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(MethodDecl n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Formal n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(VoidType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntArrayType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BooleanType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntegerType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IdentifierType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Block n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Return n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(If n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IfElse n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Switch n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(CaseSimple n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(CaseDefault n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(While n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(For n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Print n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignSimple n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignPlus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignMinus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignTimes n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignDivide n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignMod n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignAnd n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignOr n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignXor n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignLeftShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignRightShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(PostIncrement n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(PreIncrement n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(PostDecrement n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(PreDecrement n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(And n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Or n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Equal n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NotEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LessThan n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LessThanOrEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(GreaterThan n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(GreaterThanOrEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseAnd n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseOr n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseXor n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Plus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Minus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Times n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Divide n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Mod n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LeftShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(RightShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(UnsignedRightShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayLookup n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayLength n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Action n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Call n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Field n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Ternary n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(InstanceOf n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntegerLiteral n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(True n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(False n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IdentifierExp n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(This n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NewArray n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NewObject n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Not n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseNot n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Identifier n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NoOp n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NoOpExp n) {
        throw new IllegalStateException();
    }
}
