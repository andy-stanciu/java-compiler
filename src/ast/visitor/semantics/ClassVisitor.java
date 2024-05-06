package ast.visitor.semantics;

import ast.*;
import ast.visitor.Visitor;
import semantics.table.SymbolContext;
import semantics.type.*;

/**
 * Validates all method and variable declarations. This consists of:
 * 1) Defines all instance variables, initializing their types.
 * 2) Defines all methods, initializing their (return) types.
 * 3) Defines all method parameters, initializing their types.
 * 4) Defines all method local variables, initializing their types.
 */
public final class ClassVisitor implements Visitor {
    private final SymbolContext symbolContext;

    public ClassVisitor() {
        this.symbolContext = SymbolContext.getInstance();
    }

    @Override
    public void visit(Program n) {
        n.cl.forEach(c -> c.accept(this));
    }

    @Override
    public void visit(MainClass n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        if (n.conflict) return;

        symbolContext.enter(n.i.s);
        symbolContext.addEntry("this", symbolContext.lookupClass(n.i.s));
        n.vl.forEach(v -> v.accept(this));
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        if (n.conflict) return;

        symbolContext.lookupClass(n.j.s);  // extended type
        // TODO: handle extended class

        symbolContext.enter(n.i.s);
        symbolContext.addEntry("this", symbolContext.lookupClass(n.i.s));
        n.vl.forEach(v -> v.accept(this));
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(VarDecl n) {
        var varInfo = symbolContext.addVariableEntry(n.i.s);
        n.conflict = varInfo == null;
        if (n.conflict) return;

        n.t.accept(this);  // declared type
        varInfo.type = n.type = n.t.type;
    }

    @Override
    public void visit(MethodDecl n) {
        var methodInfo = symbolContext.addMethodEntry("#" + n.i.s);
        n.conflict = methodInfo == null;
        if (n.conflict) return;

        n.t.accept(this);  // return type
        methodInfo.returnType = n.type = n.t.type;

        symbolContext.enter("#" + n.i.s);

        // validate parameters and add them to method info
        n.fl.forEach(f -> {
            f.accept(this);
            if (!f.conflict) {  // only adding parameter if no name conflict
                methodInfo.addArgument(f.type);
            }
        });

        n.vl.forEach(v -> v.accept(this));  // local variables
        symbolContext.exit();
    }

    @Override
    public void visit(Formal n) {
        var varInfo = symbolContext.addVariableEntry(n.i.s);
        n.conflict = varInfo == null;
        if (n.conflict) return;

        n.t.accept(this);  // declared type
        varInfo.type = n.type = n.t.type;
    }

    @Override
    public void visit(IntArrayType n) {
        n.type = TypeIntArray.getInstance();
    }

    @Override
    public void visit(BooleanType n) {
        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(IntegerType n) {
        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(IdentifierType n) {
        if (symbolContext.lookupClass(n.s) != null) {
            n.type = new TypeReference(n.s);
        } else {
            n.type = TypeUndefined.getInstance();
        }
    }

    @Override
    public void visit(Block n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(If n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(While n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Print n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Assign n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayAssign n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(And n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LessThan n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Plus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Minus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Times n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayLookup n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayLength n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Call n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntegerLiteral n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(True n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(False n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IdentifierExp n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(This n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NewArray n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NewObject n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Not n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Identifier n) {
        throw new IllegalStateException();
    }
}
