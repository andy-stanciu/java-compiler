package ast.visitor.codegen;

import ast.*;
import ast.visitor.LazyVisitor;
import codegen.Generator;
import codegen.platform.Label;
import codegen.platform.isa.ISA;
import semantics.table.SymbolContext;

import static codegen.platform.Directive.QUAD;

public final class CodeDataVisitor extends LazyVisitor {
    private final Generator generator;
    private final SymbolContext symbolContext;

    public CodeDataVisitor(SymbolContext symbolContext, ISA isa) {
        this.generator = Generator.getInstance(isa);
        this.symbolContext = symbolContext;
    }

    @Override
    public void visit(Program n) {
        // before codegen visitor pass, need to:
        // a) initialize virtual tables
        // b) assign locations to instance variables
        symbolContext.buildVirtualTables();
        symbolContext.assignVariableOffsets();

        n.m.accept(this);

        generator.genDataSection();
        n.cl.forEach(c -> c.accept(this));
        generator.newLine();
    }

    @Override
    public void visit(MainClass n) {
        symbolContext.enterClass(n.i1.s);
        symbolContext.enterMethod("main");

        var main = symbolContext.getCurrentMethod();
        if (main == null) {
            throw new IllegalStateException();
        }

        // calculate main method stack frame size:
        // size of stack frame = local variable count
        int frameSize = main.localVariableCount();
        if (frameSize % 2 == 1) frameSize++;  // alignment
        main.frameSize = frameSize * Generator.WORD_SIZE;

        int offset = 1;
        // assign offsets to local variables
        for (int i = 0; i < main.localVariableCount(); i++) {
            main.getLocalVariable(i).vIndex = -offset++;
        }

        symbolContext.exit();
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genLabel(Label.of(n.i.s + "$$"));
        generator.genUnary(QUAD, Label.of("0"));  // no superclass
        class_.methodEntries().forEachRemaining(m ->
                generator.genUnary(QUAD, Label.of(m.getQualifiedName())));

        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        var class_ = symbolContext.lookupClass(n.i.s);
        if (class_ == null) {
            throw new IllegalStateException();
        }

        generator.genLabel(Label.of(n.i.s + "$$"));
        generator.genUnary(QUAD, Label.of(n.j.s + "$$"));  // superclass
        class_.methodEntries().forEachRemaining(m ->
                generator.genUnary(QUAD, Label.of(m.getQualifiedName())));

        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(MethodDecl n) {
        var method = symbolContext.lookupMethod(n.i.s);
        if (method == null) {
            throw new IllegalStateException();
        }

        // calculate method stack frame size:
        // size of stack frame = arg count + local variable count + obj ptr
        int frameSize = method.argumentCount() + method.localVariableCount() + 1;
        if (frameSize % 2 == 1) frameSize++;  // alignment
        method.frameSize = frameSize * Generator.WORD_SIZE;

        int offset = 2;  // start offset at 2 to leave space for obj ptr

        symbolContext.enterMethod(n.i.s);
        // assign offsets to parameters
        for (int i = 0; i < n.fl.size(); i++) {
            var p = symbolContext.lookupVariable(n.fl.get(i).i.s);
            if (p == null) {
                throw new IllegalStateException();
            }

            p.vIndex = -offset++;
        }

        // assign offsets to local variables
        for (int i = 0; i < method.localVariableCount(); i++) {
            method.getLocalVariable(i).vIndex = -offset++;
        }

        symbolContext.exit();
    }
}
