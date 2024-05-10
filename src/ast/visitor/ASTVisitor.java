package ast.visitor;

import ast.*;
import ast.visitor.util.Indenter;

public final class ASTVisitor implements Visitor {
    private final Indenter indenter;

    public ASTVisitor() {
        this.indenter = Indenter.create();
    }

    @Override
    public void visit(Program n) {
        System.out.println("Program");
        indenter.push();
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            System.out.println();
            n.cl.get(i).accept(this);
        }
        indenter.pop();
        System.out.println();
    }

    @Override
    public void visit(MainClass n) {
        indenter.print();
        System.out.print("MainClass ");
        n.i1.accept(this);
        System.out.printf(" (line %d)%n", n.line_number);
        indenter.push();
        n.s.accept(this);
        indenter.pop();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        indenter.print();
        System.out.print("Class ");
        n.i.accept(this);
        System.out.printf(" (line %d)", n.line_number);
        indenter.push();
        for (int i = 0; i < n.vl.size(); i++) {
            System.out.println();
            n.vl.get(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            System.out.println();
            n.ml.get(i).accept(this);
        }
        indenter.pop();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        indenter.print();
        System.out.print("Class ");
        n.i.accept(this);
        System.out.print(" extends ");
        n.j.accept(this);
        System.out.printf(" (line %d)", n.line_number);
        indenter.push();
        for (int i = 0; i < n.vl.size(); i++) {
            System.out.println();
            n.vl.get(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            System.out.println();
            n.ml.get(i).accept(this);
        }
        indenter.pop();
    }

    @Override
    public void visit(VarDecl n) {
        indenter.print();
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(MethodDecl n) {
        indenter.print();
        System.out.print("MethodDecl ");
        n.i.accept(this);
        System.out.printf(" (line %d)%n", n.line_number);
        indenter.push();
        indenter.print();
        System.out.print("returns ");
        n.t.accept(this);
        System.out.println();
        if (n.fl.size() > 0) {
            indenter.print();
            System.out.println("parameters:");
        }
        indenter.push();
        for (int i = 0; i < n.fl.size(); i++) {
            n.fl.get(i).accept(this);
            System.out.println();
        }
        indenter.pop();
        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.get(i).accept(this);
            System.out.println();
        }
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.get(i).accept(this);
            System.out.println();
        }
        indenter.print();
        System.out.print("Return ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.e.line_number);
        indenter.pop();
    }

    @Override
    public void visit(Formal n) {
        indenter.print();
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
    }

    @Override
    public void visit(IntArrayType n) {
        System.out.print("int[]");
    }

    @Override
    public void visit(BooleanType n) {
        System.out.print("boolean");
    }

    @Override
    public void visit(IntegerType n) {
        System.out.print("int");
    }

    @Override
    public void visit(IdentifierType n) {
        System.out.print(n.s);
    }

    @Override
    public void visit(Block n) {
        indenter.push();
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.get(i).accept(this);
            if (i + 1 < n.sl.size()) {
                System.out.println();
            }
        }
        indenter.pop();
    }

    @Override
    public void visit(If n) {
        indenter.print();
        System.out.print("If ");
        n.e.accept(this);
        System.out.printf(" (line %d)%n", n.line_number);
        if (!(n.s1 instanceof Block)) indenter.push();
        n.s1.accept(this);
        System.out.println();
        if (!(n.s1 instanceof Block)) indenter.pop();
        indenter.print();
        System.out.println("Else");
        if (!(n.s2 instanceof Block)) indenter.push();
        n.s2.accept(this);
        if (!(n.s2 instanceof Block)) indenter.pop();
    }

    @Override
    public void visit(While n) {
        indenter.print();
        System.out.print("While ");
        n.e.accept(this);
        System.out.printf(" (line %d)%n", n.line_number);
        if (!(n.s instanceof Block)) indenter.push();
        n.s.accept(this);
        if (!(n.s instanceof Block)) indenter.pop();
    }

    @Override
    public void visit(Print n) {
        indenter.print();
        System.out.printf("Print (line %d)%n", n.line_number);
        indenter.push();
        indenter.print();
        n.e.accept(this);
        indenter.pop();
    }

    @Override
    public void visit(AssignSimple n) {
        indenter.print();
        System.out.print("Assign ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignPlus n) {
        indenter.print();
        System.out.print("AssignPlus ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignMinus n) {
        indenter.print();
        System.out.print("AssignMinus ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignTimes n) {
        indenter.print();
        System.out.print("AssignTimes ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignDivide n) {
        indenter.print();
        System.out.print("AssignDivide ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignMod n) {
        indenter.print();
        System.out.print("AssignMod ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignAnd n) {
        indenter.print();
        System.out.print("AssignAnd ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignOr n) {
        indenter.print();
        System.out.print("AssignOr ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignXor n) {
        indenter.print();
        System.out.print("AssignXor ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignLeftShift n) {
        indenter.print();
        System.out.print("AssignLeftShift ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignRightShift n) {
        indenter.print();
        System.out.print("AssignRightShift ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        indenter.print();
        System.out.print("AssignUnsignedRightShift ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(PostIncrement n) {
        indenter.print();
        System.out.print("PostIncrement ");
        n.i.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(PreIncrement n) {
        indenter.print();
        System.out.print("PreIncrement ");
        n.i.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(PostDecrement n) {
        indenter.print();
        System.out.print("PostDecrement ");
        n.i.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(PreDecrement n) {
        indenter.print();
        System.out.print("PreDecrement ");
        n.i.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(ArrayAssign n) {
        indenter.print();
        System.out.print("ArrayAssign ");
        n.i.accept(this);
        System.out.print("[");
        n.e1.accept(this);
        System.out.print("]");
        System.out.print(" <- ");
        n.e2.accept(this);
        System.out.printf(" (line %d)", n.line_number);
    }

    @Override
    public void visit(And n) {
        System.out.print("And (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Or n) {
        System.out.print("Or (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Equal n) {
        System.out.print("Equal (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(NotEqual n) {
        System.out.print("NotEqual (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(LessThan n) {
        System.out.print("LessThan (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(LessThanOrEqual n) {
        System.out.print("LessThanOrEqual (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(GreaterThan n) {
        System.out.print("GreaterThan (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(GreaterThanOrEqual n) {
        System.out.print("GreaterThanOrEqual (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(BitwiseAnd n) {
        System.out.print("BitwiseAnd (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(BitwiseOr n) {
        System.out.print("BitwiseOr (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(BitwiseXor n) {
        System.out.print("BitwiseXor (");
        n.e1.accept(this);
        System.out.print(", ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Plus n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" + ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Minus n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" - ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Times n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" * ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Divide n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" / ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(Mod n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" % ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(LeftShift n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" << ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(RightShift n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" >> ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(UnsignedRightShift n) {
        System.out.print("(");
        n.e1.accept(this);
        System.out.print(" >>> ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(ArrayLookup n) {
        System.out.print("ArrayLookup ");
        n.e1.accept(this);
        System.out.print("[");
        n.e2.accept(this);
        System.out.print("]");
    }

    @Override
    public void visit(ArrayLength n) {
        System.out.print("ArrayLength ");
        n.e.accept(this);
    }

    @Override
    public void visit(Call n) {
        System.out.print("(");
        n.e.accept(this);
        System.out.print(".");
        n.i.accept(this);
        System.out.print("(");
        for (int i = 0; i < n.el.size(); i++) {
            n.el.get(i).accept(this);
            if (i + 1 < n.el.size()) {
                System.out.print(", ");
            }
        }
        System.out.print("))");
    }

    @Override
    public void visit(Ternary n) {
        System.out.print("(");
        n.c.accept(this);
        System.out.print(" ? ");
        n.e1.accept(this);
        System.out.print(" : ");
        n.e2.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(InstanceOf n) {
        System.out.print("(");
        n.e.accept(this);
        System.out.print(" instanceof ");
        n.i.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(IntegerLiteral n) {
        System.out.print(n.i);
    }

    @Override
    public void visit(True n) {
        System.out.print("true");
    }

    @Override
    public void visit(False n) {
        System.out.print("false");
    }

    @Override
    public void visit(IdentifierExp n) {
        System.out.print(n.s);
    }

    @Override
    public void visit(This n) {
        System.out.print("this");
    }

    @Override
    public void visit(NewArray n) {
        System.out.print("new int[");
        n.e.accept(this);
        System.out.print("]");
    }

    @Override
    public void visit(NewObject n) {
        System.out.print("new ");
        n.i.accept(this);
        System.out.print("()");
    }

    @Override
    public void visit(Not n) {
        System.out.print("Not ");
        n.e.accept(this);
    }

    @Override
    public void visit(BitwiseNot n) {
        System.out.print("BitwiseNot ");
        n.e.accept(this);
    }

    @Override
    public void visit(Identifier n) {
        System.out.print(n.s);
    }
}
