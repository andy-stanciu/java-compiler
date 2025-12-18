package parser.visitor;

import ast.*;
import commons.LazyVisitor;
import commons.BlockContext;
import commons.BlockType;
import commons.Indenter;
import commons.PrecedentTracker;

public final class PrettyPrintVisitor extends LazyVisitor {
    private final Indenter indenter;
    private final PrecedentTracker precedentTracker;
    private final BlockContext blockContext;

    public PrettyPrintVisitor() {
        this.indenter = Indenter.create();
        this.precedentTracker = PrecedentTracker.create();
        this.blockContext = BlockContext.create();
    }

    public void visit(Program n) {
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            System.out.println();
            n.cl.get(i).accept(this);
        }
        System.out.println();
    }

    public void visit(MainClass n) {
        System.out.print("class ");
        n.i1.accept(this);
        System.out.println(" {");
        indenter.push();
        indenter.print();
        System.out.print("public static void main(String[] ");
        n.i2.accept(this);
        System.out.println(") {");
        indenter.push();
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.get(i).accept(this);
            if (i + 1 < n.sl.size()) {
                System.out.println();
            }
        }
        System.out.println();
        indenter.pop();
        indenter.print();
        System.out.println("}");
        indenter.pop();
        System.out.print("}");
    }

    public void visit(ClassDeclSimple n) {
        System.out.print("class ");
        n.i.accept(this);
        System.out.print(" {");
        indenter.push();
        for (int i = 0; i < n.dl.size(); i++) {
            System.out.println();
            n.dl.get(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            System.out.println();
            n.ml.get(i).accept(this);
        }
        indenter.pop();
        System.out.println();
        System.out.print("}");
    }

    public void visit(ClassDeclExtends n) {
        System.out.print("class ");
        n.i.accept(this);
        System.out.print(" extends ");
        n.j.accept(this);
        System.out.print(" {");
        indenter.push();
        for (int i = 0; i < n.dl.size(); i++) {
            System.out.println();
            n.dl.get(i).accept(this);
        }
        for (int i = 0; i < n.ml.size(); i++) {
            System.out.println();
            n.ml.get(i).accept(this);
        }
        indenter.pop();
        System.out.println();
        System.out.print("}");
    }

    public void visit(VarDecl n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(VarInit n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
        System.out.print(" = ");
        n.e.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    public void visit(MethodDecl n) {
        indenter.print();
        System.out.print("public ");
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
        System.out.print("(");
        for (int i = 0; i < n.fl.size(); i++) {
            n.fl.get(i).accept(this);
            if (i + 1 < n.fl.size()) {
                System.out.print(", ");
            }
        }
        System.out.print(") {");
        indenter.push();
        for (int i = 0; i < n.sl.size(); i++) {
            System.out.println();
            n.sl.get(i).accept(this);
        }
        System.out.println();
        indenter.pop();
        indenter.print();
        System.out.print("}");
    }

    public void visit(Formal n) {
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
    }

    @Override
    public void visit(VoidType n) {
        System.out.print("void");
    }

    public void visit(ArrayType n) {
        n.t.accept(this);
        System.out.print("[]".repeat(n.dimension));
    }

    public void visit(BooleanType n) {
        System.out.print("boolean");
    }

    public void visit(IntegerType n) {
        System.out.print("int");
    }

    public void visit(StringType n) {
        System.out.print("String");
    }

    public void visit(IdentifierType n) {
        System.out.print(n.s);
    }

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
    public void visit(Return n) {
        indenter.print();
        System.out.print("return");
        if (!(n.e instanceof NoOpExp)) {
            System.out.print(" ");
        }
        n.e.accept(this);
        System.out.print(";");
    }

    @Override
    public void visit(If n) {
        indenter.print();
        System.out.print("if (");
        n.e.accept(this);
        System.out.print(")");
        System.out.println(" {");
        if (!(n.s instanceof Block)) indenter.push();
        n.s.accept(this);
        if (!(n.s instanceof Block)) indenter.pop();
        System.out.println();
        indenter.print();
        System.out.print("}");
    }

    public void visit(IfElse n) {
        indenter.print();
        System.out.print("if (");
        n.e.accept(this);
        System.out.print(")");
        System.out.println(" {");
        if (!(n.s1 instanceof Block)) indenter.push();
        n.s1.accept(this);
        if (!(n.s1 instanceof Block)) indenter.pop();
        System.out.println();
        indenter.print();
        System.out.println("} else {");
        if (!(n.s2 instanceof Block)) indenter.push();
        n.s2.accept(this);
        if (!(n.s2 instanceof Block)) indenter.pop();
        System.out.println();
        indenter.print();
        System.out.print("}");
    }

    @Override
    public void visit(Switch n) {
        indenter.print();
        System.out.print("switch (");
        n.e.accept(this);
        System.out.print(") {");
        indenter.push();
        n.cl.forEach(c -> {
            System.out.println();
            c.accept(this);
        });
        indenter.pop();
        System.out.println();
        indenter.print();
        System.out.print("}");
    }

    @Override
    public void visit(CaseSimple n) {
        indenter.print();
        System.out.print("case " + n.n + ":");
        indenter.push();
        n.sl.forEach(s -> {
            System.out.println();
            indenter.print();
            s.accept(this);
        });
        if (n.breaks) {
            System.out.println();
            indenter.print();
            System.out.print("break;");
        }
        indenter.pop();
    }

    @Override
    public void visit(CaseDefault n) {
        indenter.print();
        System.out.print("default:");
        indenter.push();
        n.sl.forEach(s -> {
            System.out.println();
            indenter.print();
            s.accept(this);
        });
        if (n.breaks) {
            System.out.println();
            indenter.print();
            System.out.print("break;");
        }
        indenter.pop();
    }

    public void visit(While n) {
        indenter.print();
        System.out.print("while (");
        n.e.accept(this);
        System.out.print(")");
        if (n.s instanceof Block) {
            System.out.println(" {");
            n.s.accept(this);
            System.out.println();
            indenter.print();
            System.out.print("}");
        } else {
            System.out.println();
            indenter.push();
            n.s.accept(this);
            indenter.pop();
        }
    }

    @Override
    public void visit(For n) {
        blockContext.push(BlockType.FOR);
        indenter.print();
        System.out.print("for (");
        n.s0.accept(this);
        System.out.print(";");
        if (!(n.e instanceof NoOpExp)) System.out.print(" ");
        n.e.accept(this);
        System.out.print(";");
        if (!(n.s1 instanceof NoOp)) System.out.print(" ");
        n.s1.accept(this);
        System.out.print(")");
        blockContext.pop();
        if (n.s2 instanceof Block) {
            System.out.println(" {");
            n.s2.accept(this);
            System.out.println();
            indenter.print();
            System.out.print("}");
        } else {
            System.out.println();
            indenter.push();
            n.s2.accept(this);
            indenter.pop();
        }
    }

    public void visit(Print n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        System.out.print("System.out.println(");
        n.e.accept(this);
        System.out.print(")");
        if (context != BlockType.FOR) System.out.print(";");
    }

    public void visit(AssignSimple n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" = ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignPlus n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" += ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignMinus n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" -= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignTimes n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" *= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignDivide n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" /= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignMod n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" %= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignAnd n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" &= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignOr n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" |= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignXor n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" ^= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignLeftShift n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" <<= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignRightShift n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" >>= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e1.accept(this);
        System.out.print(" >>>= ");
        n.e2.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(PostIncrement n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e.accept(this);
        System.out.print("++");
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(PreIncrement n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        System.out.print("++");
        n.e.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(PostDecrement n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        n.e.accept(this);
        System.out.print("--");
        if (context != BlockType.FOR) System.out.print(";");
    }

    @Override
    public void visit(PreDecrement n) {
        var context = blockContext.peek();
        if (context != BlockType.FOR) indenter.print();
        System.out.print("--");
        n.e.accept(this);
        if (context != BlockType.FOR) System.out.print(";");
    }

    public void visit(And n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" && ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(Or n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" || ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(Equal n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" == ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(NotEqual n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" != ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(LessThan n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" < ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(LessThanOrEqual n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" <= ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(GreaterThan n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" > ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(GreaterThanOrEqual n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" >= ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(BitwiseAnd n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" & ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(BitwiseOr n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" | ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(BitwiseXor n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" ^ ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(UnaryMinus n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        System.out.print("-");
        n.e.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(UnaryPlus n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        System.out.print("+");
        n.e.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(Plus n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" + ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(Minus n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" - ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(Times n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" * ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(Divide n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" / ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(Mod n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" % ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(LeftShift n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" << ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(RightShift n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" >> ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(UnsignedRightShift n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        System.out.print(" >>> ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(ArrayLookup n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e1.accept(this);
        n.el.forEach(e -> {
            System.out.print("[");
            e.accept(this);
            System.out.print("]");
        });
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(ArrayLength n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e.accept(this);
        System.out.print(".length");
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(Action n) {
        n.c.accept(this);
        System.out.print(";");
    }

    public void visit(Call n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
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
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(Field n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e.accept(this);
        System.out.print(".");
        n.i.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(Ternary n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.c.accept(this);
        System.out.print(" ? ");
        n.e1.accept(this);
        System.out.print(" : ");
        n.e2.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(InstanceOf n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        n.e.accept(this);
        System.out.print(" instanceof ");
        n.i.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(IntegerLiteral n) {
        System.out.print(n.i);
    }

    public void visit(StringLiteral n) {
        System.out.print("\"" + n.s + "\"");
    }

    public void visit(True n) {
        System.out.print("true");
    }

    public void visit(False n) {
        System.out.print("false");
    }

    public void visit(IdentifierExp n) {
        System.out.print(n.s);
    }

    public void visit(This n) {
        System.out.print("this");
    }

    public void visit(NewArray n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        System.out.print("new int");
        n.el.forEach(e -> {
            System.out.print("[");
            e.accept(this);
            System.out.print("]");
        });
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(NewObject n) {
        precedentTracker.leftParen(n);
        System.out.print("new ");
        System.out.print(n.i.s);
        System.out.print("()");
        precedentTracker.rightParen(n);
    }

    public void visit(Not n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        System.out.print("!");
        n.e.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    @Override
    public void visit(BitwiseNot n) {
        precedentTracker.leftParen(n);
        precedentTracker.push(n);
        System.out.print("~");
        n.e.accept(this);
        precedentTracker.pop();
        precedentTracker.rightParen(n);
    }

    public void visit(Identifier n) {
        System.out.print(n.s);
    }

    @Override
    public void visit(NoOp n) {
        if (blockContext.peek() != BlockType.FOR) {
            indenter.print();
            System.out.print(";");
        }
    }

    @Override
    public void visit(NoOpExp n) {}
}
