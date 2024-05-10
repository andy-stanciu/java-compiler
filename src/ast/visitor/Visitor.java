package ast.visitor;

import ast.*;

public interface Visitor {
    void visit(Program n);

    void visit(MainClass n);

    void visit(ClassDeclSimple n);

    void visit(ClassDeclExtends n);

    void visit(VarDecl n);

    void visit(MethodDecl n);

    void visit(Formal n);

    void visit(IntArrayType n);

    void visit(BooleanType n);

    void visit(IntegerType n);

    void visit(IdentifierType n);

    void visit(Block n);

    void visit(If n);

    void visit(While n);

    void visit(Print n);

    void visit(Assign n);

    void visit(ArrayAssign n);

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

    void visit(Plus n);

    void visit(Minus n);

    void visit(Times n);

    void visit(Divide n);

    void visit(Mod n);

    void visit(ArrayLookup n);

    void visit(ArrayLength n);

    void visit(Call n);

    void visit(Ternary n);

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
}
