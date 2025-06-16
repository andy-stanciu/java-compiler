package ast.visitor.dataflow;

import ast.*;
import ast.visitor.LazyVisitor;
import dataflow.DataflowGraph;
import semantics.table.SymbolContext;

import java.util.Map;
import java.util.TreeMap;

public final class DataflowVisitor extends LazyVisitor {
    private final SymbolContext symbolContext;
    private final Map<String, DataflowGraph> graphs;

    public DataflowVisitor(SymbolContext symbolContext) {
        this.symbolContext = symbolContext;
        this.graphs = new TreeMap<>();
    }

    /**
     * Prints all instruction graphs to stdout.
     */
    public void dumpInstructions() {
        graphs.forEach((m, g) -> {
            System.out.println(m + ":");
            g.printInstructions();
            System.out.println();
        });
    }

    /**
     * Prints all block graphs to stdout.
     */
    public void dumpBlocks() {
        graphs.forEach((m, g) -> {
            System.out.println(m + ":");
            g.printBlocks();
            System.out.println();
        });
    }

    @Override
    public void visit(Program n) {
        n.m.accept(this);
        n.cl.forEach(c -> c.accept(this));
    }

    @Override
    public void visit(MainClass n) {
        symbolContext.enterClass(n.i1.s);
        symbolContext.enterMethod("main");
        var m = symbolContext.getCurrentMethod();

        n.dataflow = DataflowGraph.create(symbolContext, m, n.sl)
                .build()
                .validateReturnStatements()
                .validateVariableDeclarations();

        graphs.put(m.getQualifiedName(), n.dataflow);
        symbolContext.exit();
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(VarDecl n) {
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
    }

    @Override
    public void visit(VarInit n) {
        n.t.accept(this);
        System.out.print(" ");
        n.i.accept(this);
        System.out.print(" = ");
        n.e.accept(this);
    }

    @Override
    public void visit(MethodDecl n) {
        symbolContext.enterMethod(n.i.s);
        var m = symbolContext.getCurrentMethod();

        n.dataflow = DataflowGraph.create(symbolContext, m, n.sl)
                .build()
                .validateReturnStatements()
                .validateVariableDeclarations();

        graphs.put(m.getQualifiedName(), n.dataflow);
        symbolContext.exit();
    }

    @Override
    public void visit(ArrayType n) {
        n.t.accept(this);
        System.out.print("[]".repeat(n.dimension));
    }

    @Override
    public void visit(BooleanType n) {
        System.out.print("boolean");
    }

    @Override
    public void visit(IntegerType n) {
        System.out.print("int");;
    }

    @Override
    public void visit(IdentifierType n) {
        System.out.print(n.s);
    }

    @Override
    public void visit(Block n) {
        System.out.print("block");
    }

    @Override
    public void visit(Return n) {
        System.out.print("return ");
        n.e.accept(this);
    }

    @Override
    public void visit(If n) {
        System.out.print("if not (");
        n.e.accept(this);
        System.out.print(") goto ");
    }

    @Override
    public void visit(IfElse n) {
        System.out.print("if not (");
        n.e.accept(this);
        System.out.print(") goto ");
    }

    @Override
    public void visit(Switch n) {
        System.out.print("switch (");
        n.e.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(While n) {
        System.out.print("if not (");
        n.e.accept(this);
        System.out.print(") goto ");
    }

    @Override
    public void visit(For n) {
        n.s0.accept(this);
    }

    @Override
    public void visit(Print n) {
        System.out.print("print(");
        n.e.accept(this);
        System.out.print(")");
    }

    @Override
    public void visit(AssignSimple n) {
        n.e1.accept(this);
        System.out.print(" = ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignPlus n) {
        n.e1.accept(this);
        System.out.print(" += ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignMinus n) {
        n.e1.accept(this);
        System.out.print(" -= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignTimes n) {
        n.e1.accept(this);
        System.out.print(" *= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignDivide n) {
        n.e1.accept(this);
        System.out.print(" /= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignMod n) {
        n.e1.accept(this);
        System.out.print(" %= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignAnd n) {
        n.e1.accept(this);
        System.out.print(" &= ");
        n.e1.accept(this);
    }

    @Override
    public void visit(AssignOr n) {
        n.e1.accept(this);
        System.out.print(" |= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignXor n) {
        n.e1.accept(this);
        System.out.print(" ^= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignLeftShift n) {
        n.e1.accept(this);
        System.out.print(" <<= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignRightShift n) {
        n.e1.accept(this);
        System.out.print(" >>= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        n.e1.accept(this);
        System.out.print(" >>>= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(PostIncrement n) {
        n.e.accept(this);
        System.out.print("++");
    }

    @Override
    public void visit(PreIncrement n) {
        System.out.print("++");
        n.e.accept(this);
    }

    @Override
    public void visit(PostDecrement n) {
        n.e.accept(this);
        System.out.print("--");
    }

    @Override
    public void visit(PreDecrement n) {
        System.out.print("--");
        n.e.accept(this);
    }

    @Override
    public void visit(And n) {
        n.e1.accept(this);
        System.out.print(" && ");
        n.e2.accept(this);
    }

    @Override
    public void visit(Or n) {
        n.e1.accept(this);
        System.out.print(" || ");
        n.e2.accept(this);
    }

    @Override
    public void visit(Equal n) {
        n.e1.accept(this);
        System.out.print(" == ");
        n.e2.accept(this);
    }

    @Override
    public void visit(NotEqual n) {
        n.e1.accept(this);
        System.out.print(" != ");
        n.e2.accept(this);
    }

    @Override
    public void visit(LessThan n) {
        n.e1.accept(this);
        System.out.print(" < ");
        n.e2.accept(this);
    }

    @Override
    public void visit(LessThanOrEqual n) {
        n.e1.accept(this);
        System.out.print(" <= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(GreaterThan n) {
        n.e1.accept(this);
        System.out.print(" > ");
        n.e2.accept(this);
    }

    @Override
    public void visit(GreaterThanOrEqual n) {
        n.e1.accept(this);
        System.out.print(" >= ");
        n.e2.accept(this);
    }

    @Override
    public void visit(BitwiseAnd n) {
        n.e1.accept(this);
        System.out.print(" & ");
        n.e2.accept(this);
    }

    @Override
    public void visit(BitwiseOr n) {
        n.e1.accept(this);
        System.out.print(" | ");
        n.e2.accept(this);
    }

    @Override
    public void visit(BitwiseXor n) {
        n.e1.accept(this);
        System.out.print(" ^ ");
        n.e2.accept(this);
    }

    @Override
    public void visit(UnaryMinus n) {
        System.out.print("-");
        n.e.accept(this);
    }

    @Override
    public void visit(UnaryPlus n) {
        System.out.print("-");
        n.e.accept(this);
    }

    @Override
    public void visit(Plus n) {
        n.e1.accept(this);
        System.out.print(" + ");
        n.e2.accept(this);
    }

    @Override
    public void visit(Minus n) {
        n.e1.accept(this);
        System.out.print(" - ");
        n.e2.accept(this);
    }

    @Override
    public void visit(Times n) {
        n.e1.accept(this);
        System.out.print(" * ");
        n.e2.accept(this);
    }

    @Override
    public void visit(Divide n) {
        n.e1.accept(this);
        System.out.print(" / ");
        n.e2.accept(this);
    }

    @Override
    public void visit(Mod n) {
        n.e1.accept(this);
        System.out.print(" % ");
        n.e2.accept(this);
    }

    @Override
    public void visit(LeftShift n) {
        n.e1.accept(this);
        System.out.print(" << ");
        n.e2.accept(this);
    }

    @Override
    public void visit(RightShift n) {
        n.e1.accept(this);
        System.out.print(" >> ");
        n.e2.accept(this);
    }

    @Override
    public void visit(UnsignedRightShift n) {
        n.e1.accept(this);
        System.out.print(" >>> ");
        n.e2.accept(this);
    }

    @Override
    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        n.el.forEach(e -> {
            System.out.print("[");
            e.accept(this);
            System.out.print("]");
        });
    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);
        System.out.print(".length");
    }

    @Override
    public void visit(Action n) {
        n.c.accept(this);
    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);
        System.out.print("." + n.i + "(");
        for (int i = 0; i < n.el.size(); i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            n.el.get(i).accept(this);
        }
        System.out.print(")");
    }

    @Override
    public void visit(Field n) {
        n.e.accept(this);
        System.out.print("." + n.i);
    }

    @Override
    public void visit(Ternary n) {
        n.c.accept(this);
        System.out.print(" ? ");
        n.e1.accept(this);
        System.out.print(" : ");
        n.e2.accept(this);
    }

    @Override
    public void visit(InstanceOf n) {
        n.e.accept(this);
        System.out.print(" instanceof " + n.i);
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
        System.out.print("new ");
        n.t.accept(this);
        n.el.forEach(e -> {
            System.out.print("[");
            e.accept(this);
            System.out.print("]");
        });
    }

    @Override
    public void visit(NewObject n) {
        System.out.print("new " + n.i + "()");
    }

    @Override
    public void visit(Not n) {
        System.out.print("!");
        n.e.accept(this);
    }

    @Override
    public void visit(BitwiseNot n) {
        System.out.print("~");
        n.e.accept(this);
    }

    @Override
    public void visit(Identifier n) {
        System.out.print(n.s);
    }

    @Override
    public void visit(NoOp n) {
        System.out.print("(no-op)");
    }

    @Override
    public void visit(NoOpExp n) {
        System.out.print("void");
    }
}
