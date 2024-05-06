package semantics.table;

import semantics.info.Info;

import java.util.HashMap;
import java.util.Map;

public final class SymbolTable {
    private final SymbolTable parent;
    private final Map<String, Info> symbols;
    private final TableType type;

    /**
     * Constructs a global symbol table.
     */
    public SymbolTable() {
        this(null, TableType.GLOBAL);
    }

    /**
     * Constructs a symbol table with the specified parent table and type.
     * @param parent Parent symbol table.
     * @param type Type of symbol table to create.
     */
    public SymbolTable (SymbolTable parent, TableType type) {
        this.symbols = new HashMap<>();
        this.parent = parent;
        this.type = type;
    }

    /**
     * @return Whether this table is a global symbol table.
     */
    public boolean isGlobal() {
        return type == TableType.GLOBAL;
    }

    /**
     * @return Whether this table is a class symbol table.
     */
    public boolean isClass() {
        return type == TableType.CLASS;
    }

    /**
     * @return Whether this table is a local symbol table.
     */
    public boolean isLocal() {
        return type == TableType.LOCAL;
    }

    /**
     * @return The parent of this symbol table.
     * @throws IllegalStateException If this symbol table does not have a parent.
     */
    public SymbolTable getParent() {
        if (!hasParent()) {
            throw new IllegalStateException();
        }

        return parent;
    }

    /**
     * @return Whether this symbol table has a parent.
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Adds the specified entry to this symbol table.
     * @param symbol Symbol associated with the entry.
     * @param info Information associated with the symbol.
     * @return Whether the entry was successfully added.
     */
    public boolean addEntry(String symbol, Info info) {
        if (symbol == null) return false;

        if (symbols.containsKey(symbol) && symbols.get(symbol) != null) {
            return false;
        }

        symbols.put(symbol, info);
        return true;
    }

    /**
     * Looks up the specified symbol within this table.
     * @param symbol The symbol to lookup.
     * @param report Whether the symbol should be reported and marked as undefined.
     * @return Information associated with the symbol, or null if the symbol is undefined.
     */
    public Info lookup(String symbol, boolean report) {
        if (report && !symbols.containsKey(symbol)) {
            System.err.printf("Undefined symbol: %s%n", symbol);
            symbols.put(symbol, null);  // mark as undefined
        }

        return symbols.get(symbol);
    }
}
