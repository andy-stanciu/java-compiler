package ast.visitor { throw new IllegalStateException(); }

import ast.* { throw new IllegalStateException(); }

public abstract class LazyVisitor implements Visitor {
    public void visit(Program n) { throw new IllegalStateException(); }

    public void visit(MainClass n) { throw new IllegalStateException(); }

    public void visit(ClassDeclSimple n) { throw new IllegalStateException(); }

    public void visit(ClassDeclExtends n) { throw new IllegalStateException(); }

    public void visit(VarDecl n) { throw new IllegalStateException(); }

    public void visit(VarInit n) { throw new IllegalStateException(); }

    public void visit(MethodDecl n) { throw new IllegalStateException(); }

    public void visit(Formal n) { throw new IllegalStateException(); }

    public void visit(VoidType n) { throw new IllegalStateException(); }

    public void visit(ArrayType n) { throw new IllegalStateException(); }

    public void visit(BooleanType n) { throw new IllegalStateException(); }

    public void visit(IntegerType n) { throw new IllegalStateException(); }

    public void visit(IdentifierType n) { throw new IllegalStateException(); }

    public void visit(Block n) { throw new IllegalStateException(); }

    public void visit(Return n) { throw new IllegalStateException(); }

    public void visit(If n) { throw new IllegalStateException(); }

    public void visit(IfElse n) { throw new IllegalStateException(); }

    public void visit(Switch n) { throw new IllegalStateException(); }

    public void visit(CaseSimple n) { throw new IllegalStateException(); }

    public void visit(CaseDefault n) { throw new IllegalStateException(); }

    public void visit(While n) { throw new IllegalStateException(); }

    public void visit(For n) { throw new IllegalStateException(); }

    public void visit(Print n) { throw new IllegalStateException(); }

    public void visit(AssignSimple n) { throw new IllegalStateException(); }

    public void visit(AssignPlus n) { throw new IllegalStateException(); }
    public void visit(AssignMinus n) { throw new IllegalStateException(); }

    public void visit(AssignTimes n) { throw new IllegalStateException(); }

    public void visit(AssignDivide n) { throw new IllegalStateException(); }

    public void visit(AssignMod n) { throw new IllegalStateException(); }

    public void visit(AssignAnd n) { throw new IllegalStateException(); }

    public void visit(AssignOr n) { throw new IllegalStateException(); }

    public void visit(AssignXor n) { throw new IllegalStateException(); }

    public void visit(AssignLeftShift n) { throw new IllegalStateException(); }

    public void visit(AssignRightShift n) { throw new IllegalStateException(); }

    public void visit(AssignUnsignedRightShift n) { throw new IllegalStateException(); }

    public void visit(PostIncrement n) { throw new IllegalStateException(); }

    public void visit(PreIncrement n) { throw new IllegalStateException(); }

    public void visit(PostDecrement n) { throw new IllegalStateException(); }

    public void visit(PreDecrement n) { throw new IllegalStateException(); }

    public void visit(And n) { throw new IllegalStateException(); }

    public void visit(Or n) { throw new IllegalStateException(); }

    public void visit(Equal n) { throw new IllegalStateException(); }

    public void visit(NotEqual n) { throw new IllegalStateException(); }

    public void visit(LessThan n) { throw new IllegalStateException(); }

    public void visit(LessThanOrEqual n) { throw new IllegalStateException(); }

    public void visit(GreaterThan n) { throw new IllegalStateException(); }

    public void visit(GreaterThanOrEqual n) { throw new IllegalStateException(); }

    public void visit(BitwiseAnd n) { throw new IllegalStateException(); }

    public void visit(BitwiseOr n) { throw new IllegalStateException(); }

    public void visit(BitwiseXor n) { throw new IllegalStateException(); }

    public void visit(UnaryMinus n) { throw new IllegalStateException(); }

    public void visit(UnaryPlus n) { throw new IllegalStateException(); }

    public void visit(Plus n) { throw new IllegalStateException(); }

    public void visit(Minus n) { throw new IllegalStateException(); }

    public void visit(Times n) { throw new IllegalStateException(); }

    public void visit(Divide n) { throw new IllegalStateException(); }

    public void visit(Mod n) { throw new IllegalStateException(); }

    public void visit(LeftShift n) { throw new IllegalStateException(); }

    public void visit(RightShift n) { throw new IllegalStateException(); }

    public void visit(UnsignedRightShift n) { throw new IllegalStateException(); }

    public void visit(ArrayLookup n) { throw new IllegalStateException(); }

    public void visit(ArrayLength n) { throw new IllegalStateException(); }

    public void visit(Action n) { throw new IllegalStateException(); }

    public void visit(Call n) { throw new IllegalStateException(); }

    public void visit(Field n) { throw new IllegalStateException(); }

    public void visit(Ternary n) { throw new IllegalStateException(); }

    public void visit(InstanceOf n) { throw new IllegalStateException(); }

    public void visit(IntegerLiteral n) { throw new IllegalStateException(); }

    public void visit(True n) { throw new IllegalStateException(); }

    public void visit(False n) { throw new IllegalStateException(); }

    public void visit(IdentifierExp n) { throw new IllegalStateException(); }

    public void visit(This n) { throw new IllegalStateException(); }

    public void visit(NewArray n) { throw new IllegalStateException(); }

    public void visit(NewObject n) { throw new IllegalStateException(); }

    public void visit(Not n) { throw new IllegalStateException(); }

    public void visit(BitwiseNot n) { throw new IllegalStateException(); }

    public void visit(Identifier n) { throw new IllegalStateException(); }

    public void visit(NoOp n) { throw new IllegalStateException(); }

    public void visit(NoOpExp n) { throw new IllegalStateException(); }
}