package AST.Visitor;

import AST.*;

public class ASTVisitor implements Visitor {
    @Override
    public void visit(Program n) {
        System.out.println("Program");
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            System.out.println();
            n.cl.get(i).accept(this);
        }
    }

    @Override
    public void visit(MainClass n) {
        System.out.print("  MainClass ");
        n.i1.accept(this);
        System.out.println();
        System.out.print("    ");
        n.s.accept(this);
        System.out.println();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        System.out.println("  Class ");
        n.i.accept(this);
        System.out.println();
        for (int i = 0; i < n.vl.size(); i++) {
            System.out.print("    ");
            n.vl.get(i).accept(this);
            if (i + 1 < n.vl.size()) {
                System.out.println();
            }
        }
        for (int i = 0; i < n.ml.size(); i++) {
            System.out.println();
            n.ml.get(i).accept(this);
        }
        System.out.println();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        System.out.println("  Class ");
        n.i.accept(this);
        System.out.println(" extends ");
        n.j.accept(this);
        System.out.println();
        for (int i = 0; i < n.vl.size(); i++) {
            System.out.print("    ");
            n.vl.get(i).accept(this);
            if (i + 1 < n.vl.size()) {
                System.out.println();
            }
        }
        for (int i = 0; i < n.ml.size(); i++) {
            System.out.println();
            n.ml.get(i).accept(this);
        }
        System.out.println();
    }

    @Override
    public void visit(VarDecl n) {
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
    }

    @Override
    public void visit(MethodDecl n) {
        System.out.println("    MethodDecl ");
        n.i.accept(this);
        System.out.println();
        System.out.print("      returns ");
        n.t.accept(this);
        System.out.println();
        if (n.fl.size() > 0) {
            System.out.println("      parameters:");
        }
        for (int i = 0; i < n.fl.size(); i++) {
            n.fl.get(i).accept(this);
            if (i + 1 < n.fl.size()) {
                System.out.println();
            }
        }
        for (int i = 0; i < n.vl.size(); i++) {
            System.out.print("      ");
            n.vl.get(i).accept(this);
            System.out.println();
        }
        for (int i = 0; i < n.sl.size(); i++) {
            System.out.print("      ");
            n.sl.get(i).accept(this);
            System.out.println();
        }
        System.out.print("      Return ");
        n.e.accept(this);
    }

    @Override
    public void visit(Formal n) {
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
        for (int i = 0; i < n.sl.size(); i++) {
            System.out.print("  ");
            n.sl.get(i).accept(this);
            System.out.println();
        }
    }

    @Override
    public void visit(If n) {
        System.out.print("If ");
    }

    @Override
    public void visit(While n) {

    }

    @Override
    public void visit(Print n) {

    }

    @Override
    public void visit(Assign n) {

    }

    @Override
    public void visit(ArrayAssign n) {

    }

    @Override
    public void visit(And n) {

    }

    @Override
    public void visit(LessThan n) {

    }

    @Override
    public void visit(Plus n) {

    }

    @Override
    public void visit(Minus n) {

    }

    @Override
    public void visit(Times n) {

    }

    @Override
    public void visit(ArrayLookup n) {

    }

    @Override
    public void visit(ArrayLength n) {

    }

    @Override
    public void visit(Call n) {

    }

    @Override
    public void visit(IntegerLiteral n) {

    }

    @Override
    public void visit(True n) {

    }

    @Override
    public void visit(False n) {

    }

    @Override
    public void visit(IdentifierExp n) {

    }

    @Override
    public void visit(This n) {

    }

    @Override
    public void visit(NewArray n) {

    }

    @Override
    public void visit(NewObject n) {

    }

    @Override
    public void visit(Not n) {

    }

    @Override
    public void visit(Identifier n) {

    }
}
