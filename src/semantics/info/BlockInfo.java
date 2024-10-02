package semantics.info;

import semantics.table.SymbolContext;
import semantics.table.SymbolTable;
import semantics.table.TableType;

public final class BlockInfo extends Info {
    private final SymbolTable table;

    public BlockInfo(SymbolTable parent, String name) {
        super(name);
        this.table = new SymbolTable(parent, TableType.LOCAL, parent.getName() +
                SymbolContext.BLOCK_PREFIX + name);
    }

    /**
     * @return The qualified method name, i.e. Class$Method$i.
     */
    public String getQualifiedName() {
        return table.getName();
    }

    public SymbolTable getTable() {
        return table;
    }
}
