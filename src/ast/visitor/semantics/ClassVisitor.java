package ast.visitor.semantics;

import ast.*;
import ast.visitor.Visitor;
import codegen.Generator;
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
        n.m.accept(this);
        n.cl.forEach(c -> c.accept(this));

        // after this visitor finishes visiting all the classes, the
        // inheritance graph will be constructed and valid. we need to now
        // propagate method declarations from base classes to derived classes
        symbolContext.propagateInheritedMembers();
    }

    @Override
    public void visit(MainClass n) {
        symbolContext.enterClass(n.i1.s);
        symbolContext.enterMethod("main");

        var m = symbolContext.getCurrentMethod();
        m.lineNumber = n.line_number;
        m.endLineNumber = n.endPos.getLine();

        n.sl.forEach(s -> s.accept(this));

        symbolContext.exit();
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        if (n.conflict) return;

        var this_ = symbolContext.lookupClass(n.i.s);  // this class
        if (this_ == null) {
            throw new IllegalSemanticException("unreachable");
        }

        symbolContext.enterClass(n.i.s);
        symbolContext.addEntry("this", this_);  // point to this class
        n.dl.forEach(v -> v.accept(this));  // instance variables
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

        symbolContext.enterClass(n.i.s);
        symbolContext.addEntry("this", derived);  // point to derived class
        n.dl.forEach(v -> v.accept(this));  // instance variables
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
        n.t.accept(this);  // declared type
        var varInfo = symbolContext.addVariableEntry(n.i.s);
        n.type = n.t.type;
        if (symbolContext.hasCurrentMethod()) {
            n.method = symbolContext.getCurrentMethod();
        }

        if (varInfo != null) {  // if there was no conflict
            varInfo.type = n.type;
            if (symbolContext.isMethod()) {
                var method = symbolContext.getCurrentMethod();
                // all block local variables added to method
                method.addLocalVariable(varInfo);
            } else if (symbolContext.isClass()) {
                var class_ = symbolContext.getCurrentClass();
                class_.addInstanceVariable(varInfo);
            }
        }
    }

    @Override
    public void visit(VarInit n) {
        n.t.accept(this);  // declared type
        var varInfo = symbolContext.addVariableEntry(n.i.s);
        n.type = n.t.type;
        if (symbolContext.hasCurrentMethod()) {
            n.method = symbolContext.getCurrentMethod();
        }

        if (varInfo != null) {  // if there was no conflict
            varInfo.type = n.type;
            varInfo.initializer = n.e;
            if (symbolContext.isMethod()) {
                var method = symbolContext.getCurrentMethod();
                // all block local variables added to method
                method.addLocalVariable(varInfo);
            } else if (symbolContext.isClass()) {
                var class_ = symbolContext.getCurrentClass();
                class_.addInstanceVariable(varInfo);
            }
        }
    }

    @Override
    public void visit(MethodDecl n) {
        var methodInfo = symbolContext.addMethodEntry(n.i.s);
        n.conflict = methodInfo == null;
        if (n.conflict) return;

        n.t.accept(this);  // return type
        methodInfo.returnType = n.type = n.t.type;
        methodInfo.lineNumber = n.line_number;
        methodInfo.endLineNumber = n.endPos.getLine();

        symbolContext.enterMethod(n.i.s);

        // validate parameters first
        n.fl.forEach(f -> {
            f.accept(this);
            // adding parameter regardless of whether its name conflicts with
            // another parameter
            methodInfo.addArgument(f.i.s, f.type);
        });

        // does the method have more than 5 parameters? If so, we don't allow
        // this for now...
        if (methodInfo.argumentCount() > Generator.ARGUMENT_REGISTERS.length) {
            logger.logError("Encountered %d arguments for method \"%s\" (too many!)%n",
                    methodInfo.argumentCount(), n.i);
        }

        // visit local variable declarations and blocks among all statements
        n.sl.forEach(s -> s.accept(this));
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
    public void visit(VoidType n) {
        n.type = TypeVoid.getInstance();
    }

    @Override
    public void visit(ArrayType n) {
        n.t.accept(this);
        n.type = new TypeArray((TypeSingular) n.t.type, n.dimension);
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
        n.blockInfo = symbolContext.addBlockEntry();
        n.method = symbolContext.getCurrentMethod();

        symbolContext.enterBlock(n.blockInfo);
        n.sl.forEach(s -> s.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(Return n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(If n) {
        n.method = symbolContext.getCurrentMethod();
        n.s.accept(this);
    }

    @Override
    public void visit(IfElse n) {
        n.method = symbolContext.getCurrentMethod();
        n.s1.accept(this);
        n.s2.accept(this);
    }

    @Override
    public void visit(Switch n) {
        n.method = symbolContext.getCurrentMethod();
        n.cl.forEach(c -> c.accept(this));
    }

    @Override
    public void visit(CaseSimple n) {
        n.sl.forEach(s -> s.accept(this));
    }

    @Override
    public void visit(CaseDefault n) {
        n.sl.forEach(s -> s.accept(this));
    }

    @Override
    public void visit(While n) {
        n.method = symbolContext.getCurrentMethod();
        n.s.accept(this);
    }

    @Override
    public void visit(For n) {
        n.method = symbolContext.getCurrentMethod();
        n.blockInfo = symbolContext.addBlockEntry();

        symbolContext.enterBlock(n.blockInfo);
        n.s0.accept(this);
        n.s1.accept(this);
        n.s2.accept(this);
        symbolContext.exit();
    }

    @Override
    public void visit(Print n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignSimple n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignPlus n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignMinus n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignTimes n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignDivide n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignMod n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignAnd n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignOr n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignXor n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignLeftShift n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignRightShift n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(PostIncrement n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(PreIncrement n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(PostDecrement n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(PreDecrement n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(And n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Or n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Equal n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NotEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LessThan n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LessThanOrEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(GreaterThan n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(GreaterThanOrEqual n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseAnd n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseOr n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BitwiseXor n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(UnaryMinus n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(UnaryPlus n) {
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
    public void visit(Divide n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Mod n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(LeftShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(RightShift n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(UnsignedRightShift n) {
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
    public void visit(Action n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(Call n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Field n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Ternary n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(InstanceOf n) {
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
    public void visit(BitwiseNot n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Identifier n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NoOp n) {
        n.method = symbolContext.getCurrentMethod();
    }

    @Override
    public void visit(NoOpExp n) {
        throw new IllegalStateException();
    }
}
