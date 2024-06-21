package semantics.info;

import codegen.Generator;
import semantics.table.SymbolContext;
import semantics.table.SymbolTable;
import semantics.table.TableType;
import semantics.type.Type;

import java.util.ArrayList;
import java.util.List;

public final class MethodInfo extends Info {
    public Type returnType;
    public int vIndex;  // index into vtable
    public int frameSize;  // frame size of method
    public MethodInfo overridden;  // pointer to overridden method, or null if not overriding
    public int lineNumber;
    public int endLineNumber;
    private final List<Type> argumentTypes;
    private final SymbolTable table;

    public MethodInfo(SymbolTable parent, String name) {
        super(name);
        this.table = new SymbolTable(parent, TableType.LOCAL, parent.getName() +
                SymbolContext.METHOD_PREFIX + name);
        this.argumentTypes = new ArrayList<>();
    }

    /**
     * @return The offset in the vtable.
     */
    public int getOffset() {
        if (vIndex == 0) {
            throw new IllegalStateException();
        }

        return vIndex * Generator.WORD_SIZE;
    }

    /**
     * @return The qualified method name, i.e. Class$Method.
     */
    public String getQualifiedName() {
        return table.getName();
    }

    public int argumentCount() {
        return argumentTypes.size();
    }

    public void addArgument(Type type) {
        argumentTypes.add(type);
    }

    public Type getArgument(int i) {
        return argumentTypes.get(i);
    }

    public SymbolTable getTable() {
        return table;
    }
}
