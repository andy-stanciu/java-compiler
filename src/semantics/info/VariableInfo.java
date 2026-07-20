package semantics.info;

import ast.Expression;
import codegen.Generator;
import lombok.Getter;
import semantics.table.SymbolTable;
import semantics.type.Type;

public final class VariableInfo extends Info {
    public Type type;
    public Expression initializer;
    public int vIndex;  // index of variable, either as an instance variable or parameter/local variable

    @Getter
    private final SymbolTable parent;

    private final boolean isInstanceVariable;
    public int lineNumber;  // line number where the variable was defined

    public VariableInfo(SymbolTable parent, String name,
                        boolean isInstanceVariable) {
        super(name);
        this.parent = parent;
        this.isInstanceVariable = isInstanceVariable;
    }

    /**
     * @return Whether this variable has an initializer.
     */
    public boolean hasInitializer() {
        return initializer != null;
    }

    /**
     * @return Whether this variable is an instance variable.
     */
    public boolean isInstanceVariable() {
        return isInstanceVariable;
    }

    /**
     * @return If this is a parameter or local variable, returns offset from the stack frame.
     *         If this is an instance variable, use {@link VariableInfo#getInstanceVariableOffset()}
     */
    public int getOffset() {
        if (vIndex == 0) {
            throw new IllegalStateException();
        }

        return vIndex * Generator.WORD_SIZE;
    }

    /**
     * @return Returns offset of instance variable from start of the object.
     */
    public int getInstanceVariableOffset() {
        return getOffset();
    }
}
