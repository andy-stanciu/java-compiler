package AST.Visitor;

import AST.*;

public class ASTVisitor implements Visitor {
    private final Indenter indenter;

    public ASTVisitor() {
        this.indenter = Indenter.create(2);
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
    }

    @Override
    public void visit(MainClass n) {
        indenter.print();
        System.out.print("MainClass ");
        n.i1.accept(this);
        System.out.println();
        indenter.push();
        n.s.accept(this);
        indenter.pop();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        indenter.print();
        System.out.print("Class ");
        n.i.accept(this);
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
        System.out.println();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        indenter.print();
        System.out.print("Class ");
        n.i.accept(this);
        System.out.print(" extends ");
        n.j.accept(this);
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
        System.out.println();
    }

    @Override
    public void visit(VarDecl n) {
        indenter.print();
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
    }

    @Override
    public void visit(MethodDecl n) {
        indenter.print();
        System.out.print("MethodDecl ");
        n.i.accept(this);
        System.out.println();
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
        System.out.println();
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
        System.out.println();
        if (!(n.s instanceof Block)) indenter.push();
        n.s.accept(this);
        if (!(n.s instanceof Block)) indenter.pop();
    }

    @Override
    public void visit(Print n) {
        indenter.print();
        System.out.println("Print");
        indenter.push();
        indenter.print();
        n.e.accept(this);
        indenter.pop();
    }

    @Override
    public void visit(Assign n) {
        indenter.print();
        System.out.print("Assign ");
        n.i.accept(this);
        System.out.print(" <- ");
        n.e.accept(this);
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
    public void visit(LessThan n) {
        System.out.print("LessThan (");
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
        System.out.print("new int [");
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
    public void visit(Identifier n) {
        System.out.print(n.s);
    }
}
