package ast.visitor.semantics;

import ast.*;
import ast.visitor.Visitor;
import semantics.Logger;
import semantics.table.SymbolContext;
import semantics.type.*;
import semantics.type.Type;

import java.util.HashSet;
import java.util.Set;

import static codegen.Generator.ARGUMENT_REGISTERS;

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
    }

    @Override
    public void visit(MainClass n) {
        symbolContext.enterClass(n.i1.s);
        symbolContext.enterMethod("main");
        n.sl.forEach(s -> s.accept(this));  // main method statement(s)
        symbolContext.exit();
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        if (n.conflict) return;

        symbolContext.enterClass(n.i.s);
        n.dl.forEach(d -> d.accept(this));
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        if (n.conflict) return;

        symbolContext.enterClass(n.i.s);
        n.dl.forEach(d -> d.accept(this));
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(VarDecl n) {}

    @Override
    public void visit(VarInit n) {
        n.e.accept(this);  // initialization expression
        if (!n.e.eval().type.isAssignableTo(n.type)) {
            logger.logError("Cannot assign %s to %s%n",
                    n.e.eval().type, n.type);
        }
    }

    @Override
    public void visit(MethodDecl n) {
        if (n.conflict) return;

        symbolContext.enterMethod(n.i.s);
        n.sl.forEach(s -> s.accept(this));  // method statements

        symbolContext.exit();
    }

    @Override
    public void visit(Formal n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(VoidType n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(ArrayType n) {
        throw new IllegalStateException();
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
        symbolContext.enterBlock(n.blockInfo);
        n.sl.forEach(s -> s.accept(this));  // block statements
        symbolContext.exit();
    }

    @Override
    public void visit(Return n) {
        var m = symbolContext.getCurrentMethod();
        n.e.accept(this);

        // is the return expression assignable to the return type?
        if (!n.e.eval().type.isAssignableTo(m.returnType)) {
            logger.logError("Method \"%s\" expected to return %s, but provided %s%n",
                    m.name, m.returnType, n.e.eval().type);
        }

        // if returning void, then the return expression MUST be a no-op
        // (as opposed to another void-returning method, for example)
        if (m.returnType.equals(TypeVoid.getInstance()) && !(n.e instanceof NoOpExp)) {
            logger.logError("Cannot return a value from a method with void result type%n");
        }
    }

    @Override
    public void visit(If n) {
        n.e.accept(this);
        if (!n.e.eval().type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("If statement condition expected boolean, but provided %s%n",
                    n.e.eval().type);
        }

        n.s.accept(this);  // if statement(s)
    }

    @Override
    public void visit(IfElse n) {
        n.e.accept(this);
        if (!n.e.eval().type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("If statement condition expected boolean, but provided %s%n",
                    n.e.eval().type);
        }

        n.s1.accept(this);  // if statement(s)
        n.s2.accept(this);  // else statement(s)
    }

    @Override
    public void visit(Switch n) {
        n.e.accept(this);
        if (!n.e.eval().type.equals(TypeInt.getInstance())) {
            logger.logError("Switch expected int, but provided %s%n", n.e.eval().type);
        }

        Set<Integer> cases = new HashSet<>();
        boolean foundDefault = false;

        for (var c : n.cl) {
            c.accept(this);

            if (c instanceof CaseSimple case_) {
                if (cases.contains(case_.n)) {
                    logger.logError("Duplicate case label %d%n", case_.n);
                }
                cases.add(case_.n);
            } else if (c instanceof CaseDefault) {
                if (foundDefault) {
                    logger.logError("Duplicate default label%n");
                }
                foundDefault = true;
            }
        }
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
        n.e.accept(this);
        if (!n.e.eval().type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("While loop condition expected boolean, but provided %s%n",
                    n.e.eval().type);
        }

        n.s.accept(this);  // loop body statement(s)
    }

    @Override
    public void visit(For n) {
        symbolContext.enterBlock(n.blockInfo);
        n.s0.accept(this);  // initializer clause
        n.e.accept(this);  // condition clause

        if (!n.e.eval().type.equals(TypeBoolean.getInstance()) &&
                !n.e.eval().type.equals(TypeVoid.getInstance())) {
            // loop condition can either be boolean, or void (will loop indefinitely)
            logger.logError("For loop condition provided %s%n", n.e.eval().type);
        }

        n.s1.accept(this);  // incrementer clause
        n.s2.accept(this);  // loop body statement(s)
        symbolContext.exit();
    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        if (!n.e.eval().type.isAssignableTo(TypeInt.getInstance())) {
            logger.logError("Print statement expected int, but provided %s%n",
                    n.e.eval().type);
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
        visitBinaryExpAllEqualTypes(n, "&", TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(BitwiseOr n) {
        visitBinaryExpAllEqualTypes(n, "|", TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(BitwiseXor n) {
        visitBinaryExpAllEqualTypes(n, "^", TypeInt.getInstance(), TypeBoolean.getInstance());
    }

    @Override
    public void visit(UnaryMinus n) {
        n.e.accept(this);
        if (!n.e.eval().type.equals(TypeInt.getInstance())) {
            logger.logError("Operator - cannot be applied to %s%n", n.e.eval().type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(UnaryPlus n) {
        n.e.accept(this);
        if (!n.e.eval().type.equals(TypeInt.getInstance())) {
            logger.logError("Operator + cannot be applied to %s%n", n.e.eval().type);
        }

        n.type = TypeInt.getInstance();
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
        n.e1.accept(this);  // array expression

        if (n.e1.eval().type instanceof TypeArray arr && n.el.size() <= arr.dimension) {
            n.el.forEach(e -> {
                e.accept(this);
                if (!e.eval().type.isAssignableTo(TypeInt.getInstance())) {
                    logger.logError("Array index expected an int, but provided %s%n",
                            e.eval().type);
                }
            });

            if (n.el.size() == arr.dimension) {
                n.type = arr.type;
            } else {
                n.type = new TypeArray(arr.type, arr.dimension - n.el.size());
            }
        } else {
            logger.logError("Cannot index on non-array type %s%n", n.e1.eval().type);
            n.type = TypeUndefined.getInstance();
        }
    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);  // int array expression
        if (!n.e.eval().type.isArray()) {
            logger.logError("Cannot get length of non-array type %s%n", n.e.eval().type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(Action n) {
        n.c.accept(this);
        if (!n.c.type.equals(TypeVoid.getInstance()) &&
                !n.c.type.isUndefined()) {
            logger.logWarning("Result type %s of \"%s\" is ignored%n", n.c.type, n.c.i.s);
        }
    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);                   // object type
        n.el.forEach(e -> e.accept(this));  // parameters

        n.type = TypeUndefined.getInstance();  // mark as undefined for now

        if (n.e.eval().type == TypeUndefined.getInstance()) {
            // if undefined, cannot invoke method so we skip
            return;
        }

        if (n.e.eval().type instanceof TypeObject obj) {
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
                    var actual = n.el.get(i).eval().type;
                    var formal = m.getArgument(i);
                    if (!actual.isAssignableTo(formal)) {
                        logger.logError("Cannot assign %s to %s for argument %d of method \"%s\"%n",
                                actual, formal, i + 1, n.i);
                    }
                }
            }
        } else {
            logger.logError("Expected reference type for method invocation, but provided %s%n",
                    n.e.eval().type);
        }
    }

    @Override
    public void visit(Field n) {
        n.e.accept(this);                   // object type
        n.type = TypeUndefined.getInstance();  // mark as undefined for now

        if (n.e.eval().type == TypeUndefined.getInstance()) {
            // if undefined, cannot access field so we skip
            return;
        }

        if (n.e.eval().type instanceof TypeObject obj) {
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
                    n.e.eval().type);
        }
    }

    @Override
    public void visit(Ternary n) {
        n.c.accept(this);  // bool condition
        if (!n.c.eval().type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("Ternary condition expected boolean, but provided %s%n",
                    n.c.eval().type);
        }

        n.e1.accept(this);  // true expression
        n.e2.accept(this);  // false expression

        if (!n.e1.eval().type.equals(n.e2.eval().type)) {
            logger.logError("Ternary expression cannot be applied to %s, %s%n",
                    n.e1.eval().type, n.e2.eval().type);
            n.type = TypeUndefined.getInstance();
        } else {
            n.type = n.e1.eval().type;
        }
    }

    @Override
    public void visit(InstanceOf n) {
        n.e.accept(this); // identifier expression

        // Lookup identifier in LHS, check that it's a class
        var c = symbolContext.lookupClass(n.i.s);

        if (c != null) {
            var obj = new TypeObject(c);
            if (!n.e.eval().type.isAssignableTo(obj) && !obj.isAssignableTo(n.e.eval().type)) {
                logger.logError("Inconvertible types %s, %s for instanceof%n",
                        n.e.eval().type, n.i.s);
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
        n.t.accept(this);  // singular type
        n.el.forEach(e -> {
            e.accept(this);
            if (!e.eval().type.isAssignableTo(TypeInt.getInstance())) {
                logger.logError("Array instantiation expected an int, but provided %s%n",
                        e.eval().type);
            }
        });

        if (n.el.size() > ARGUMENT_REGISTERS.length) {
            logger.logError("Encountered %d size arguments for %s array (too many!)%n",
                    n.el.size(), n.t);
        }

        n.type = new TypeArray((TypeSingular) n.t.type, n.el.size());
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
        if (!n.e.eval().type.isAssignableTo(TypeBoolean.getInstance())) {
            logger.logError("Operator ! cannot be applied to %s%n", n.e.eval().type);
        }

        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(BitwiseNot n) {
        n.e.accept(this);  // int expression
        if (!n.e.eval().type.isAssignableTo(TypeInt.getInstance())) {
            logger.logError("Operator ~ cannot be applied to %s%n", n.e.eval().type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(Identifier n) {
        throw new IllegalStateException();
    }

    @Override
    public void visit(NoOp n) {}

    @Override
    public void visit(NoOpExp n) {
        n.type = TypeVoid.getInstance();
    }

    /**
     * Visits the specified increment/decrement statement.
     * @param n The increment/decrement statement.
     * @param sym The symbol associated with the increment/decrement statement.
     */
    private void visitIncrement(Increment n, String sym) {
        n.e.accept(this);  // assignable

        if (!(n.e instanceof Assignable)) {
            logger.logError("Cannot assign to expression%n");
        } else if (!n.e.eval().type.equals(TypeInt.getInstance())) {
            logger.logError("Cannot apply operator %s to %s%n",
                    sym, n.e.eval().type);
        }
    }

    /**
     * Visits the specified assignment statement.
     * @param n The assignment statement.
     * @param sym The symbol associated with the assignment statement.
     * @param accepted The types to accept (optional).
     */
    private void visitAssign(Assign n, String sym, Type... accepted) {
        n.e2.accept(this);  // RHS
        n.e1.accept(this);  // assignable

        if (accepted != null && accepted.length > 0) {
            boolean legal = false;
            for (var type : accepted) {
                if (n.e2.eval().type.equals(type)) {
                    legal = true;
                    break;
                }
            }

            if (!legal) {
                logger.logError("Assignment operator %s cannot be applied to %s%n",
                        sym, n.e2.eval().type);
                return;
            }
        }

        if (!(n.e1 instanceof Assignable)) {
            logger.logError("Cannot assign to expression%n");
        } else if (!n.e2.eval().type.isAssignableTo(n.e1.eval().type)) {
            logger.logError("Cannot assign %s to %s%n", n.e2.eval().type, n.e1.eval().type);
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
            if (n.e1.eval().type.equals(type) && n.e2.eval().type.equals(type)) {
                legal = true;
                break;
            }
        }

        if (!legal) {
            logger.logError("Operator %s cannot be applied to %s, %s%n",
                    sym, n.e1.eval().type, n.e2.eval().type);
        }

        n.type = result;
    }

    /**
     * Visits the specified binary expression that expects LHS and RHS types
     * to be equal.
     * @param n The binary expression to visit.
     * @param sym The symbol associated with the binary expression.
     * @param result The result type of the binary expression.
     * @param accepted The types to accept (optional). If a type is accepted,
     *                 both the LHS and RHS must have that type.
     */
    private void visitBinaryExpEqualTypes(BinaryExp n, String sym, Type result, Type... accepted) {
        n.e1.accept(this);
        n.e2.accept(this);

        if (!n.e1.eval().type.equals(n.e2.eval().type)) {
            logger.logError("Operator %s cannot be applied to %s, %s%n",
                    sym, n.e1.eval().type, n.e2.eval().type);
        } else {
            if (accepted != null && accepted.length > 0) {
                boolean legal = false;
                for (var type : accepted) {
                    if (n.e1.eval().type.equals(type)) {
                        legal = true;
                    }
                }

                if (!legal) {
                    logger.logError("Operator %s cannot be applied to %s, %s%n",
                            sym, n.e1.eval().type, n.e2.eval().type);
                }
            }
        }

        n.type = result;
    }

    /**
     * Visits the specified binary expression that expects LHS and RHS types
     * to be equal, setting the result type to the same type.
     * @param n The binary expression to visit.
     * @param sym The symbol associated with the binary expression.
     * @param accepted The types to accept. If a type is accepted,
     *                 both the LHS and RHS must have that type and the result
     *                 becomes that type.
     */
    private void visitBinaryExpAllEqualTypes(BinaryExp n, String sym, Type... accepted) {
        if (accepted == null) {
            throw new IllegalStateException();
        }

        n.e1.accept(this);
        n.e2.accept(this);

        if (!n.e1.eval().type.equals(n.e2.eval().type)) {
            logger.logError("Operator %s cannot be applied to %s, %s%n",
                    sym, n.e1.eval().type, n.e2.eval().type);
        } else {
            for (var type : accepted) {
                if (n.e1.eval().type.equals(type)) {
                    n.type = type;
                    return;
                }
            }

            logger.logError("Operator %s cannot be applied to %s, %s%n",
                    sym, n.e1.eval().type, n.e2.eval().type);
        }

        n.type = TypeUndefined.getInstance();
    }
}
