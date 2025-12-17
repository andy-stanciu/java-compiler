package ast.visitor;

import ast.*;

public abstract class LazyVisitor implements Visitor {
    public void visit(Program n) { throw new UnsupportedOperationException(); }

    public void visit(MainClass n) { throw new UnsupportedOperationException(); }

    public void visit(ClassDeclSimple n) { throw new UnsupportedOperationException(); }

    public void visit(ClassDeclExtends n) { throw new UnsupportedOperationException(); }

    public void visit(VarDecl n) { throw new UnsupportedOperationException(); }

    public void visit(VarInit n) { throw new UnsupportedOperationException(); }

    public void visit(MethodDecl n) { throw new UnsupportedOperationException(); }

    public void visit(Formal n) { throw new UnsupportedOperationException(); }

    public void visit(VoidType n) { throw new UnsupportedOperationException(); }

    public void visit(ArrayType n) { throw new UnsupportedOperationException(); }

    public void visit(BooleanType n) { throw new UnsupportedOperationException(); }

    public void visit(IntegerType n) { throw new UnsupportedOperationException(); }

    public void visit(StringType n) { throw new UnsupportedOperationException(); }

    public void visit(IdentifierType n) { throw new UnsupportedOperationException(); }

    public void visit(Block n) { throw new UnsupportedOperationException(); }

    public void visit(Return n) { throw new UnsupportedOperationException(); }

    public void visit(If n) { throw new UnsupportedOperationException(); }

    public void visit(IfElse n) { throw new UnsupportedOperationException(); }

    public void visit(Switch n) { throw new UnsupportedOperationException(); }

    public void visit(CaseSimple n) { throw new UnsupportedOperationException(); }

    public void visit(CaseDefault n) { throw new UnsupportedOperationException(); }

    public void visit(While n) { throw new UnsupportedOperationException(); }

    public void visit(For n) { throw new UnsupportedOperationException(); }

    public void visit(Print n) { throw new UnsupportedOperationException(); }

    public void visit(AssignSimple n) { throw new UnsupportedOperationException(); }

    public void visit(AssignPlus n) { throw new UnsupportedOperationException(); }
    public void visit(AssignMinus n) { throw new UnsupportedOperationException(); }

    public void visit(AssignTimes n) { throw new UnsupportedOperationException(); }

    public void visit(AssignDivide n) { throw new UnsupportedOperationException(); }

    public void visit(AssignMod n) { throw new UnsupportedOperationException(); }

    public void visit(AssignAnd n) { throw new UnsupportedOperationException(); }

    public void visit(AssignOr n) { throw new UnsupportedOperationException(); }

    public void visit(AssignXor n) { throw new UnsupportedOperationException(); }

    public void visit(AssignLeftShift n) { throw new UnsupportedOperationException(); }

    public void visit(AssignRightShift n) { throw new UnsupportedOperationException(); }

    public void visit(AssignUnsignedRightShift n) { throw new UnsupportedOperationException(); }

    public void visit(PostIncrement n) { throw new UnsupportedOperationException(); }

    public void visit(PreIncrement n) { throw new UnsupportedOperationException(); }

    public void visit(PostDecrement n) { throw new UnsupportedOperationException(); }

    public void visit(PreDecrement n) { throw new UnsupportedOperationException(); }

    public void visit(And n) { throw new UnsupportedOperationException(); }

    public void visit(Or n) { throw new UnsupportedOperationException(); }

    public void visit(Equal n) { throw new UnsupportedOperationException(); }

    public void visit(NotEqual n) { throw new UnsupportedOperationException(); }

    public void visit(LessThan n) { throw new UnsupportedOperationException(); }

    public void visit(LessThanOrEqual n) { throw new UnsupportedOperationException(); }

    public void visit(GreaterThan n) { throw new UnsupportedOperationException(); }

    public void visit(GreaterThanOrEqual n) { throw new UnsupportedOperationException(); }

    public void visit(BitwiseAnd n) { throw new UnsupportedOperationException(); }

    public void visit(BitwiseOr n) { throw new UnsupportedOperationException(); }

    public void visit(BitwiseXor n) { throw new UnsupportedOperationException(); }

    public void visit(UnaryMinus n) { throw new UnsupportedOperationException(); }

    public void visit(UnaryPlus n) { throw new UnsupportedOperationException(); }

    public void visit(Plus n) { throw new UnsupportedOperationException(); }

    public void visit(Minus n) { throw new UnsupportedOperationException(); }

    public void visit(Times n) { throw new UnsupportedOperationException(); }

    public void visit(Divide n) { throw new UnsupportedOperationException(); }

    public void visit(Mod n) { throw new UnsupportedOperationException(); }

    public void visit(LeftShift n) { throw new UnsupportedOperationException(); }

    public void visit(RightShift n) { throw new UnsupportedOperationException(); }

    public void visit(UnsignedRightShift n) { throw new UnsupportedOperationException(); }

    public void visit(ArrayLookup n) { throw new UnsupportedOperationException(); }

    public void visit(ArrayLength n) { throw new UnsupportedOperationException(); }

    public void visit(Action n) { throw new UnsupportedOperationException(); }

    public void visit(Call n) { throw new UnsupportedOperationException(); }

    public void visit(Field n) { throw new UnsupportedOperationException(); }

    public void visit(Ternary n) { throw new UnsupportedOperationException(); }

    public void visit(InstanceOf n) { throw new UnsupportedOperationException(); }

    public void visit(IntegerLiteral n) { throw new UnsupportedOperationException(); }

    public void visit(StringLiteral n) { throw new UnsupportedOperationException(); }

    public void visit(True n) { throw new UnsupportedOperationException(); }

    public void visit(False n) { throw new UnsupportedOperationException(); }

    public void visit(IdentifierExp n) { throw new UnsupportedOperationException(); }

    public void visit(This n) { throw new UnsupportedOperationException(); }

    public void visit(NewArray n) { throw new UnsupportedOperationException(); }

    public void visit(NewObject n) { throw new UnsupportedOperationException(); }

    public void visit(Not n) { throw new UnsupportedOperationException(); }

    public void visit(BitwiseNot n) { throw new UnsupportedOperationException(); }

    public void visit(Identifier n) { throw new UnsupportedOperationException(); }

    public void visit(NoOp n) { throw new UnsupportedOperationException(); }

    public void visit(NoOpExp n) { throw new UnsupportedOperationException(); }
}