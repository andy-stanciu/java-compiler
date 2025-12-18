package semantics.info;

import lombok.Getter;
import semantics.table.SymbolContext;
import semantics.table.SymbolTable;
import semantics.table.TableType;

@Getter
public final class BlockInfo extends Info {
    private final SymbolTable table;

    public BlockInfo(SymbolTable parent, String name) {
        super(name);
        this.table = new SymbolTable(parent, TableType.LOCAL, parent.getName() +
                SymbolContext.BLOCK_PREFIX + name);
    }
}
