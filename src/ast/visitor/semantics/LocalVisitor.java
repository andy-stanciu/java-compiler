package ast.visitor.semantics;

import ast.*;
import ast.visitor.Visitor;
import semantics.IllegalSemanticException;
import semantics.table.SymbolContext;
import semantics.type.*;

/**
 * TODO: describe what this does
 */
public final class LocalVisitor implements Visitor {
    private final SymbolContext symbolContext;

    public LocalVisitor() {
        this.symbolContext = SymbolContext.getInstance();
    }

    @Override
    public void visit(Program n) {
        n.m.accept(this);
        n.cl.forEach(c -> c.accept(this));
    }

    @Override
    public void visit(MainClass n) {
        symbolContext.enter("#main");
        n.s.accept(this);  // main method statement
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclSimple n) {
        if (n.conflict) return;

        symbolContext.enter(n.i.s);
        n.ml.forEach(m -> m.accept(this));
        symbolContext.exit();
    }

    @Override
    public void visit(ClassDeclExtends n) {
        if (n.conflict) return;

        symbolContext.enter(n.i.s);
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

        symbolContext.enter("#" + n.i.s);
        n.sl.forEach(s -> s.accept(this));  // method statements
        n.e.accept(this);                   // return expression

        // is the return expression assignable to the return type?
        if (n.e.type.isAssignableTo(n.type)) {
            System.err.printf("Method \"%s\" expected to return %s, but provided %s%n",
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
        if (n.e.type != TypeBoolean.getInstance()) {
            System.err.printf("If statement condition expected boolean, but provided %s%n",
                    n.e.type);
        }

        n.s1.accept(this);  // if statement(s)
        n.s2.accept(this);  // else statement(s)
    }

    @Override
    public void visit(While n) {
        n.e.accept(this);
        if (n.e.type != TypeBoolean.getInstance()) {
            System.err.printf("While loop condition expected boolean, but provided %s%n",
                    n.e.type);
        }

        n.s.accept(this);  // loop body statement(s)
    }

    @Override
    public void visit(Print n) {
        n.e.accept(this);
        if (n.e.type != TypeInt.getInstance()) {
            System.err.printf("Print statement expected int, but provided %s%n",
                    n.e.type);
        }
    }

    @Override
    public void visit(Assign n) {
        n.e.accept(this);  // RHS

        // Lookup identifier in LHS, check assignment compatibility
        var v = symbolContext.lookupVariable(n.i.s);
        if (v != null && !n.e.type.isAssignableTo(v.type)) {
            System.err.printf("Cannot assign %s to %s%n", n.e.type, v.type);
        } else {
            System.err.printf("Cannot assign to \"%s\". Is it a variable?%n", n.i.s);
        }
    }

    @Override
    public void visit(ArrayAssign n) {
        n.e2.accept(this);  // RHS
        n.e1.accept(this);  // indexing expression

        if (n.e2.type != TypeInt.getInstance()) {
            System.err.printf("Array assignment expected an int, but provided %s%n",
                    n.e2.type);
        }

        if (n.e1.type != TypeInt.getInstance()) {
            System.err.printf("Array index expected an int, but provided %s%n",
                    n.e1.type);
        }

        // Lookup identifier in LHS, check it is an int[]
        var v = symbolContext.lookupVariable(n.i.s);
        if (v != null && v.type != TypeIntArray.getInstance()) {
            System.err.printf("Cannot index \"%s\". Is it an array?%n", n.i.s);
        } else {
            System.err.printf("Cannot assign to \"%s\". Is it a variable?%n", n.i.s);
        }
    }

    @Override
    public void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);

        if (n.e1.type != TypeBoolean.getInstance() || n.e2.type != TypeBoolean.getInstance()) {
            System.err.printf("Operator && cannot be applied to %s, %s%n",
                    n.e1.type, n.e2.type);
        }

        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(LessThan n) {
        n.e1.accept(this);
        n.e2.accept(this);

        if (n.e1.type != TypeInt.getInstance() || n.e2.type != TypeInt.getInstance()) {
            System.err.printf("Operator < cannot be applied to %s, %s%n",
                    n.e1.type, n.e2.type);
        }

        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(Plus n) {
        n.e1.accept(this);
        n.e2.accept(this);

        if (n.e1.type != TypeInt.getInstance() || n.e2.type != TypeInt.getInstance()) {
            System.err.printf("Operator + cannot be applied to %s, %s%n",
                    n.e1.type, n.e2.type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);

        if (n.e1.type != TypeInt.getInstance() || n.e2.type != TypeInt.getInstance()) {
            System.err.printf("Operator - cannot be applied to %s, %s%n",
                    n.e1.type, n.e2.type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);

        if (n.e1.type != TypeInt.getInstance() || n.e2.type != TypeInt.getInstance()) {
            System.err.printf("Operator * cannot be applied to %s, %s%n",
                    n.e1.type, n.e2.type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(ArrayLookup n) {
        n.e1.accept(this);  // int array expression
        n.e2.accept(this);  // indexing expression

        if (n.e1.type != TypeIntArray.getInstance()) {
            System.err.printf("Cannot index on %s, expected an int[]%n", n.e1.type);
        }

        if (n.e2.type != TypeInt.getInstance()) {
            System.err.printf("Array index expected an int, but provided %s%n",
                    n.e2.type);
        }

        // only supporting int[] for now
        n.type = TypeIntArray.getInstance();
    }

    @Override
    public void visit(ArrayLength n) {
        n.e.accept(this);  // int array expression
        if (n.e.type != TypeIntArray.getInstance()) {
            System.err.printf("Cannot get length of %s, expected an int[]%n", n.e.type);
        }

        n.type = TypeInt.getInstance();
    }

    @Override
    public void visit(Call n) {
        n.e.accept(this);                   // object type
        n.el.forEach(e -> e.accept(this));  // parameters

        n.type = TypeUndefined.getInstance();  // mark as undefined for now

        if (n.e.type instanceof TypeObject obj) {
            var m = symbolContext.lookupMethod("#" + n.i, obj.base);
            if (m == null) {
                System.err.printf("Cannot resolve method \"%s\" in \"%s\"%n",
                        n.i, obj.base.name);
                return;
            }

            n.type = m.returnType;  // call node type = method return type

            if (m.argumentCount() != n.el.size()) {
                System.err.printf("Expected %d arguments for method \"%s\", but got %d",
                        m.argumentCount(), n.i, n.el.size());
            } else {
                // argument counts now match. verify that they are all assignable
                for (int i = 0; i < m.argumentCount(); i++) {
                    var actual = n.el.get(i).type;
                    var formal = m.getArgument(i);
                    if (!actual.isAssignableTo(formal)) {
                        System.err.printf("Cannot assign %s to %s for argument %d of method \"%s\"%n",
                                actual, formal, i + 1, n.i);
                    }
                }
            }
        } else {
            System.err.printf("Expected reference type for method invocation, but provided %s%n",
                    n.e.type);
        }
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
            System.err.printf("Cannot interpret \"%s\" as an expression. Is it a variable?%n",
                    n.s);
            n.type = TypeUndefined.getInstance(); // mark as undefined
        }
    }

    @Override
    public void visit(This n) {
        var this_ = symbolContext.lookupClass("this");
        if (this_ == null) {
            throw new IllegalSemanticException("unreachable");
        }

        n.type = new TypeObject(this_);
    }

    @Override
    public void visit(NewArray n) {
        n.e.accept(this);  // array length
        if (n.e.type != TypeInt.getInstance()) {
            System.err.printf("Array instantiation expected an int, but provided %s%n",
                    n.e.type);
        }

        n.type = TypeIntArray.getInstance();
    }

    @Override
    public void visit(NewObject n) {
        var classInfo = symbolContext.lookupClass(n.i.s);
        if (classInfo == null) {
            System.err.printf("Cannot resolve class \"%s\"%n", n.i.s);
            n.type = TypeUndefined.getInstance();
        } else {
            n.type = new TypeObject(classInfo);
        }
    }

    @Override
    public void visit(Not n) {
        n.e.accept(this);  // bool expression
        if (n.e.type != TypeBoolean.getInstance()) {
            System.err.printf("Operator ! cannot be applied to %s%n", n.e.type);
        }

        n.type = TypeBoolean.getInstance();
    }

    @Override
    public void visit(Identifier n) {
        throw new IllegalStateException();
    }
}
