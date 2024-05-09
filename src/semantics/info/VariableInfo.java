package semantics.info;

import semantics.table.SymbolTable;
import semantics.type.Type;

public final class VariableInfo extends Info {
    public Type type;
    private final SymbolTable parent;

    public VariableInfo(SymbolTable parent, String name) {
        super(name);
        this.parent = parent;
    }

    public SymbolTable getParent() {
        return parent;
    }
}
