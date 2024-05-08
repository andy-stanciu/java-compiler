package semantics.info;

import semantics.table.SymbolTable;
import semantics.table.TableType;
import semantics.type.Type;

import java.util.ArrayList;
import java.util.List;

public final class MethodInfo extends Info {
    public Type returnType;
    private final List<Type> argumentTypes;
    private final SymbolTable table;

    public MethodInfo(SymbolTable parent, String name) {
        super(name);
        this.table = new SymbolTable(parent, TableType.LOCAL, parent.getName() + name);
        this.argumentTypes = new ArrayList<>();
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
