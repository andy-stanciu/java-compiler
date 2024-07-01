package ast.visitor;

import ast.*;

public interface Visitor {
    void visit(Program n);

    void visit(MainClass n);

    void visit(ClassDeclSimple n);

    void visit(ClassDeclExtends n);

    void visit(VarDecl n);

    void visit(VarInit n);

    void visit(MethodDecl n);

    void visit(Formal n);

    void visit(VoidType n);

    void visit(IntArrayType n);

    void visit(BooleanType n);

    void visit(IntegerType n);

    void visit(IdentifierType n);

    void visit(Block n);

    void visit(Return n);

    void visit(If n);

    void visit(IfElse n);

    void visit(Switch n);

    void visit(CaseSimple n);

    void visit(CaseDefault n);

    void visit(While n);

    void visit(For n);

    void visit(Print n);

    void visit(AssignSimple n);

    void visit(AssignPlus n);
    void visit(AssignMinus n);

    void visit(AssignTimes n);

    void visit(AssignDivide n);

    void visit(AssignMod n);

    void visit(AssignAnd n);

    void visit(AssignOr n);

    void visit(AssignXor n);

    void visit(AssignLeftShift n);

    void visit(AssignRightShift n);

    void visit(AssignUnsignedRightShift n);

    void visit(PostIncrement n);

    void visit(PreIncrement n);

    void visit(PostDecrement n);

    void visit(PreDecrement n);

    void visit(And n);

    void visit(Or n);

    void visit(Equal n);

    void visit(NotEqual n);

    void visit(LessThan n);

    void visit(LessThanOrEqual n);

    void visit(GreaterThan n);

    void visit(GreaterThanOrEqual n);

    void visit(BitwiseAnd n);

    void visit(BitwiseOr n);

    void visit(BitwiseXor n);

    void visit(UnaryMinus n);

    void visit(UnaryPlus n);

    void visit(Plus n);

    void visit(Minus n);

    void visit(Times n);

    void visit(Divide n);

    void visit(Mod n);

    void visit(LeftShift n);

    void visit(RightShift n);

    void visit(UnsignedRightShift n);

    void visit(ArrayLookup n);

    void visit(ArrayLength n);

    void visit(Action n);

    void visit(Call n);

    void visit(Field n);

    void visit(Ternary n);

    void visit(InstanceOf n);

    void visit(IntegerLiteral n);

    void visit(True n);

    void visit(False n);

    void visit(IdentifierExp n);

    void visit(This n);

    void visit(NewArray n);

    void visit(NewObject n);

    void visit(Not n);

    void visit(BitwiseNot n);

    void visit(Identifier n);

    void visit(NoOp n);

    void visit(NoOpExp n);
}
