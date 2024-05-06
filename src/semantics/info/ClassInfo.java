package semantics.info;

import semantics.table.SymbolTable;
import semantics.table.TableType;

public final class ClassInfo extends Info {
    public final boolean main;
    private final SymbolTable table;

    public ClassInfo(SymbolTable parent, boolean main) {
        this.table = new SymbolTable(parent, TableType.CLASS);
        this.main = main;
    }

    public SymbolTable getTable() {
        return table;
    }
}
