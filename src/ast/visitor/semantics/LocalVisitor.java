package ast.visitor.semantics;

import ast.*;
import ast.visitor.Visitor;
import semantics.Logger;
import semantics.table.SymbolContext;
import semantics.type.*;
import semantics.type.Type;

/**
 * Final visitor pass of semantic analysis. Verifies that types match
 * for all expressions, variables are in scope, etc.
 */
public final class LocalVisitor implements Visitor {
    private final SymbolContext symbolContext;
    private final Logger logger;

    public LocalVisitor(SymbolContext symbolContext) {
        this.symbolContext = symbolContext;
        this.logger = Logger.getInstance();
    }

    @Override
    public void visit(Program n) {
        n.m.accept(this);
        n.cl.forEach(c -> c.accept(this));

        // after this visitor is done, we have completed static semantic
        // analysis, so we dump the symbol tables!
        symbolContext.dump();
    }

    @Override
    public void visit(MainClass n) {
        symbolContext.enterClass(n.i1.s);
        symbolContext.enterMethod("main");
        n.s.accept(this);  // main method statement
        symbolContext.exit();
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        if (n.conflict) return;

        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        if (n.conflict) return;

        symbolContext.enterClass(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(VarDecl n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(MethodDecl n) {
        if (n.conflict) return;

        symbolContext.enterMethod(n.i.s);
        n.sl.forEach(s -> s.accept(this));  // method statements
        n.e.accept(this);                   // return expression

        // is the return expression assignable to the return type?
        if (!n.e.type.isAssignableTo(n.type)) {
            logger.logError("Method \"%s\" expected to return %s, but provided %s%n",
                    n.i.s, n.type, n.e.type);
        }

        symbolContext.exit();
    }

    @Override
    public void visit(Formal n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntArrayType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(BooleanType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IntegerType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(IdentifierType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(Block n) {
        n.sl.forEach(s -> s.accept(this));  // block statements
    }

    @Override
    public void visit(If n) {
        n.e.accept(this);
        if (!n.e.type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("If statement condition expected boolean, but provided %s%n",
                    n.e.type);
        }

        n.s1.accept(this);  // if statement(s)
        n.s2.accept(this);  // else statement(s)
    }

    @Override
    public void visit(While n) {
        n.e.accept(this);
        if (!n.e.type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("While loop condition expected boolean, but provided %s%n",
                    n.e.type);
        }

        n.s.accept(this);  // loop body statement(s)
    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        if (!n.e.type.isAssignableTo(TypeInt.getInstance())) {
            logger.logError("Print statement expected int, but provided %s%n",
                    n.e.type);
        }
    }

    @Override
    public void visit(AssignSimple n) {
        visitAssign(n, "=");
    }

    @Override
    public void visit(AssignPlus n) {
        visitAssign(n, "+=", TypeInt.getInstance());
    }

    @Override
    public void visit(AssignMinus n) {
        visitAssign(n, "-=", TypeInt.getInstance());
    }

    @Override
    public void visit(AssignTimes n) {
        visitAssign(n, "*=", TypeInt.getInstance());
    }

    @Override
    public void visit(AssignDivide n) {
        visitAssign(n, "/=", TypeInt.getInstance());
    }

    @Override
    public void visit(AssignMod n) {
        visitAssign(n, "%=", TypeInt.getInstance());
    }

    @Override
    public void visit(AssignAnd n) {
        visitAssign(n, "&=", TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(AssignOr n) {
        visitAssign(n, "|=", TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(AssignXor n) {
        visitAssign(n, "^=", TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(AssignLeftShift n) {
        visitAssign(n, "<<=", TypeInt.getInstance());
    }

    @Override
    public void visit(AssignRightShift n) {
        visitAssign(n, ">>=", TypeInt.getInstance());
    }

    @Override
    public void visit(AssignUnsignedRightShift n) {
        visitAssign(n, ">>>=", TypeInt.getInstance());
    }

    @Override
    public void visit(PostIncrement n) {
        visitIncrement(n, "++");
    }

    @Override
    public void visit(PreIncrement n) {
        visitIncrement(n, "++");
    }

    @Override
    public void visit(PostDecrement n) {
        visitIncrement(n, "--");
    }

    @Override
    public void visit(PreDecrement n) {
        visitIncrement(n, "--");
    }

    @Override
    public void visit(And n) {
        visitBinaryExp(n, "&&", TypeBoolean.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(Or n) {
        visitBinaryExp(n, "||", TypeBoolean.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(Equal n) {
        visitBinaryExpEqualTypes(n, "==", TypeBoolean.getInstance());
    }

    @Override
    public void visit(NotEqual n) {
        visitBinaryExpEqualTypes(n, "!=", TypeBoolean.getInstance());
    }

    @Override
    public void visit(LessThan n) {
        visitBinaryExp(n, "<", TypeBoolean.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(LessThanOrEqual n) {
        visitBinaryExp(n, "<=", TypeBoolean.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(GreaterThan n) {
        visitBinaryExp(n, ">", TypeBoolean.getInstance(), TypeInt.getInstance());
    }
    @Override
    public void visit(GreaterThanOrEqual n) {
        visitBinaryExp(n, ">=", TypeBoolean.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(BitwiseAnd n) {
        visitBinaryExpEqualTypes(n, "&", TypeInt.getInstance(),
                TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(BitwiseOr n) {
        visitBinaryExpEqualTypes(n, "|", TypeInt.getInstance(),
                TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(BitwiseXor n) {
        visitBinaryExpEqualTypes(n, "^", TypeInt.getInstance(),
                TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(Plus n) {
        visitBinaryExp(n, "+", TypeInt.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(Minus n) {
        visitBinaryExp(n, "-", TypeInt.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(Times n) {
        visitBinaryExp(n, "*", TypeInt.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(Divide n) {
        visitBinaryExp(n, "/", TypeInt.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(Mod n) {
        visitBinaryExp(n, "%", TypeInt.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(LeftShift n) {
        visitBinaryExp(n, "<<", TypeInt.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(RightShift n) {
        visitBinaryExp(n, ">>", TypeInt.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(UnsignedRightShift n) {
        visitBinaryExp(n, ">>>", TypeInt.getInstance(), TypeInt.getInstance());
    }

    @Override
    public void visit(ArrayLookup n) {
        n.e1.accept(this);  // int array expression
        n.e2.accept(this);  // indexing expression

        if (!n.e1.type.isAssignableTo(TypeIntArray.getInstance())) {
            logger.logError("Cannot index on %s, expected an int[]%n", n.e1.type);
        }

        if (!n.e2.type.isAssignableTo(TypeInt.getInstance())) {
            logger.logError("Array index expected an int, but provided %s%n",
                    n.e2.type);
        }

        // only supporting int[] for now
        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);  // int array expression
        if (!n.e.type.isAssignableTo(TypeIntArray.getInstance())) {
            logger.logError("Cannot get length of %s, expected an int[]%n", n.e.type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);                   // object type
        n.el.forEach(e -> e.accept(this));  // parameters

        n.type = TypeUndefined.getInstance();  // mark as undefined for now

        if (n.e.type == TypeUndefined.getInstance()) {
            // if undefined, cannot invoke method so we skip
            return;
        }

        if (n.e.type instanceof TypeObject obj) {
            var m = symbolContext.lookupMethod(n.i.s, obj.base);
            if (m == null) {
                if (!symbolContext.isUndefined(n.i.s)) {
                    logger.logError("Cannot resolve method \"%s\" in \"%s\"%n",
                            n.i, obj.base.name);
                }
                return;
            }

            n.type = m.returnType;  // call node type = method return type

            if (m.argumentCount() != n.el.size()) {
                logger.logError("Expected %d arguments for method \"%s\", but provided %d%n",
                        m.argumentCount(), n.i, n.el.size());
            } else {
                // argument counts now match. verify that they are all assignable
                for (int i = 0; i < m.argumentCount(); i++) {
                    var actual = n.el.get(i).type;
                    var formal = m.getArgument(i);
                    if (!actual.isAssignableTo(formal)) {
                        logger.logError("Cannot assign %s to %s for argument %d of method \"%s\"%n",
                                actual, formal, i + 1, n.i);
                    }
                }
            }
        } else {
            logger.logError("Expected reference type for method invocation, but provided %s%n",
                    n.e.type);
        }
    }

    @Override
    public void visit(Field n) {
        n.e.accept(this);                   // object type
        n.type = TypeUndefined.getInstance();  // mark as undefined for now

        if (n.e.type == TypeUndefined.getInstance()) {
            // if undefined, cannot access field so we skip
            return;
        }

        if (n.e.type instanceof TypeObject obj) {
            var v = symbolContext.lookupInstanceVariable(n.i.s, obj.base);
            if (v == null) {
                if (!symbolContext.isUndefined(n.i.s)) {
                    logger.logError("Cannot resolve field \"%s\" in \"%s\"%n",
                            n.i.s, obj.base.name);
                }
                return;
            }

            n.type = v.type;  // field node type = instance variable type
        } else {
            logger.logError("Expected reference type for field access, but provided %s%n",
                    n.e.type);
        }
    }

    @Override
    public void visit(Ternary n) {
        n.c.accept(this);  // bool condition
        if (!n.c.type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("Ternary condition expected boolean, but provided %s%n",
                    n.c.type);
        }

        n.e1.accept(this);  // true expression
        n.e2.accept(this);  // false expression

        if (!n.e1.type.equals(n.e2.type)) {
            logger.logError("Ternary expression cannot be applied to %s, %s%n",
                    n.e1.type, n.e2.type);
            n.type = TypeUndefined.getInstance();
        } else {
            n.type = n.e1.type;
        }
    }

    @Override
    public void visit(InstanceOf n) {
        n.e.accept(this); // identifier expression

        // Lookup identifier in LHS, check that it's a class
        var c = symbolContext.lookupClass(n.i.s);

        if (c != null) {
            var obj = new TypeObject(c);
            if (!n.e.type.isAssignableTo(obj) && !obj.isAssignableTo(n.e.type)) {
                logger.logError("Inconvertible types %s, %s for instanceof%n",
                        n.e.type, n.i.s);
            }
        } else {
            if (!symbolContext.isUndefined(n.i.s)) {
                logger.logError("Cannot apply instanceof to \"%s\". Is it a class?%n",
                        n.i.s);
            }
        }

        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(IntegerLiteral n) {
        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(True n) {
        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(False n) {
        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(IdentifierExp n) {
        var v = symbolContext.lookupVariable(n.s);
        if (v != null) {
            n.type = v.type;
        } else {
            if (!symbolContext.isUndefined(n.s)) {
                logger.logError("Cannot interpret \"%s\" as an expression. Is it a variable?%n",
                        n.s);
            }
            n.type = TypeUndefined.getInstance(); // mark as undefined
        }
    }

    @Override
    public void visit(This n) {
        n.type = new TypeObject(symbolContext.getCurrentClass());
    }

    @Override
    public void visit(NewArray n) {
        n.e.accept(this);  // array length
        if (!n.e.type.isAssignableTo(TypeInt.getInstance())) {
            logger.logError("Array instantiation expected an int, but provided %s%n",
                    n.e.type);
        }

        n.type = TypeIntArray.getInstance();
    }

    @Override
    public void visit(NewObject n) {
        var classInfo = symbolContext.lookupClass(n.i.s);
        if (classInfo == null) {
            if (!symbolContext.isUndefined(n.i.s)) {
                logger.logError("Cannot resolve class \"%s\"%n", n.i.s);
            }
            n.type = TypeUndefined.getInstance();
        } else {
            n.type = new TypeObject(classInfo);
        }
    }

    @Override
    public void visit(Not n) {
        n.e.accept(this);  // bool expression
        if (!n.e.type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("Operator ! cannot be applied to %s%n", n.e.type);
        }

        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(BitwiseNot n) {
        n.e.accept(this);  // int expression
        if (!n.e.type.isAssignableTo(TypeInt.getInstance()) &&
                !n.e.type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("Operator ~ cannot be applied to %s%n", n.e.type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(Identifier n) {
        throw new IllegalStateException();
    }

    /**
     * Visits the specified increment/decrement statement.
     * @param n The increment/decrement statement.
     * @param sym The symbol associated with the increment/decrement statement.
     */
    private void visitIncrement(Increment n, String sym) {
        n.a.accept(this);  // assignable

        if (!n.a.getAssignableType().equals(TypeInt.getInstance())) {
            logger.logError("Cannot apply operator %s to %s%n",
                    sym, n.a.getAssignableType());
        }
    }

    /**
     * Visits the specified assignment statement.
     * @param n The assignment statement.
     * @param sym The symbol associated with the assignment statement.
     * @param accepted The types to accept (optional).
     */
    private void visitAssign(Assign n, String sym, Type... accepted) {
        n.e.accept(this);  // RHS
        n.a.accept(this);  // assignable

        if (accepted != null && accepted.length > 0) {
            boolean legal = false;
            for (var type : accepted) {
                if (n.e.type.equals(type)) {
                    legal = true;
                    break;
                }
            }

            if (!legal) {
                logger.logError("Assignment operator %s cannot be applied to %s%n",
                        sym, n.e.type);
                return;
            }
        }

        if (!n.e.type.isAssignableTo(n.a.getAssignableType())) {
            logger.logError("Cannot assign %s to %s%n", n.e.type, n.a.getAssignableType());
        }
    }

    /**
     * Visits the specified binary expression.
     * @param n The binary expression to visit.
     * @param sym The symbol associated with the binary expression.
     * @param result The result type of the binary expression.
     * @param expected The expected type(s) of the binary expression.
     */
    private void visitBinaryExp(BinaryExp n, String sym, Type result, Type... expected) {
        if (expected == null) {
            throw new IllegalArgumentException("Missing expected type");
        }

        n.e1.accept(this);
        n.e2.accept(this);

        boolean legal = false;
        for (var type : expected) {
            if (n.e1.type.isAssignableTo(type) && n.e2.type.isAssignableTo(type)) {
                legal = true;
                break;
            }
        }

        if (!legal) {
            logger.logError("Operator %s cannot be applied to %s, %s%n",
                    sym, n.e1.type, n.e2.type);
        }

        n.type = result;
    }

    /**
     * Visits the specified binary expression that expected LHS and RHS types
     * to be equal.
     * @param n The binary expression to visit.
     * @param sym The symbol associated with the binary expression.
     * @param result The result type of the binary expression.
     * @param accepted The types to accept (optional).
     */
    private void visitBinaryExpEqualTypes(BinaryExp n, String sym, Type result, Type... accepted) {
        n.e1.accept(this);
        n.e2.accept(this);

        if (!n.e1.type.equals(n.e2.type)) {
            logger.logError("Operator %s cannot be applied to %s, %s%n",
                    sym, n.e1.type, n.e2.type);
        }

        if (accepted != null && accepted.length > 0) {
            for (var type : accepted) {
                if (n.e1.type.equals(type)) {
                    n.type = result;
                    return;
                }
            }

            logger.logError("Operator %s cannot be applied to %s, %s%n",
                    sym, n.e1.type, n.e2.type);
            n.type = TypeUndefined.getInstance();
        } else {
            n.type = result;
        }
    }
}
