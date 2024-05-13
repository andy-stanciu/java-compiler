package ast.visitor.semantics;

import ast.*;
import ast.visitor.Visitor;
import semantics.IllegalSemanticException;
import semantics.Logger;
import semantics.info.ClassInfo;
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
    private final Logger logger;

    public ClassVisitor(SymbolContext symbolContext) {
        this.symbolContext = symbolContext;
        this.logger = Logger.getInstance();
    }

    @Override
    public void visit(Program n) {
        n.cl.forEach(c -> c.accept(this));

        // after this visitor finishes visiting all the classes, the
        // inheritance graph will be constructed and valid. we need to now
        // propagate method declarations from base classes to derived classes
        symbolContext.propagateInheritedMembers();
    }

    @Override
    public void visit(MainClass n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        if (n.conflict) return;

        var this_ = symbolContext.lookupClass(n.i.s);  // this class
        if (this_ == null) {
            throw new IllegalSemanticException("unreachable");
        }

        symbolContext.enter(n.i.s);
        symbolContext.addEntry("this", this_); // point to this class
        n.vl.forEach(v -> v.accept(this));
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        if (n.conflict) return;

        var base = symbolContext.lookupClass(n.j.s);     // base class
        var derived = symbolContext.lookupClass(n.i.s);  // derived class
        if (derived == null) {
            throw new IllegalSemanticException("unreachable");
        }

        if (base != null) {
            // if a cycle is found, derived class is left as a base class
            if (!findCycle(base, derived)) {
                base.addChild(derived);
                derived.setParent(base);
            }
        } else {
            logger.logError("Cannot resolve class \"%s\"%n", n.j.s);
        }

        symbolContext.enter(n.i.s);
        symbolContext.addEntry("this", derived);  // point to derived class
        n.vl.forEach(v -> v.accept(this));
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    /**
     * Determines whether there is a cycle between the base class and the
     * derived class, i.e. if the derived class or any of its children at one
     * point reference the base class, a cycle exists.
     * @param base The base class.
     * @param derived The derived class.
     * @return Whether a cycle exists.
     */
    private boolean findCycle(ClassInfo base, ClassInfo derived) {
        // if the base class == derived class or the base class is a child of
        // the derived class, we have a cycle (direct/indirect)
        if (base == derived || derived.getChildren().contains(base)) {
            logger.logError("Cyclic inheritance detected for class \"%s\"%n",
                    base.name);
            return true;
        }

        for (var child : derived.getChildren()) {
            if (findCycle(base, child)) {
                return true;
            }
        }
        return false;
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

        // validate parameters first
        n.fl.forEach(f -> {
            f.accept(this);
            // adding parameter regardless of whether its name conflicts with
            // another parameter
            methodInfo.addArgument(f.type);
        });

        n.vl.forEach(v -> v.accept(this));  // local variables
        symbolContext.exit();
    }

    @Override
    public void visit(Formal n) {
        n.t.accept(this);  // declared type
        n.type = n.t.type;

        var varInfo = symbolContext.addVariableEntry(n.i.s);
        if (varInfo != null) { // if there's no name conflict
            varInfo.type = n.type;
        }
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
        var class_ = symbolContext.lookupClass(n.s);
        if (class_ != null) {
            n.type = new TypeObject(class_);
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
