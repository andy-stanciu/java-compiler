package semantics.table;

import semantics.Logger;
import semantics.info.ClassInfo;
import semantics.info.Info;
import semantics.info.MethodInfo;
import semantics.info.VariableInfo;

import java.util.*;

public final class SymbolTable {
    private final SymbolTable parent;
    private final Logger logger;
    private final Map<String, Info> symbols;
    private final TableType type;
    private final String name;

    /**
     * Constructs a global symbol table.
     */
    public SymbolTable() {
        this(null, TableType.GLOBAL, "Global");
    }

    /**
     * Constructs a symbol table with the specified parent table and type.
     *
     * @param parent Parent symbol table.
     * @param type   Type of symbol table to create.
     */
    public SymbolTable(SymbolTable parent, TableType type, String name) {
        this.symbols = new HashMap<>();
        this.logger = Logger.getInstance();
        this.parent = parent;
        this.type = type;
        this.name = name;
    }

    /**
     * @return The name of this symbol table.
     */
    public String getName() {
        return name;
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
     *
     * @param symbol Symbol associated with the entry.
     * @param info   Information associated with the symbol.
     * @return Whether the entry was successfully added.
     */
    public boolean addEntry(String symbol, Info info) {
        if (symbol == null) return false;

        if (symbols.containsKey(symbol)) {
            return false;
        }

        symbols.put(symbol, info);
        return true;
    }

    /**
     * Looks up the specified symbol within this table.
     *
     * @param symbol The symbol to lookup.
     * @param report Whether the symbol should be reported and marked as undefined.
     * @return Information associated with the symbol, or null if the symbol is undefined.
     */
    public Info lookup(String symbol, boolean report) {
        if (report && !symbols.containsKey(symbol)) {
            logger.logError("Undefined symbol: \"%s\"%n", symbol);
        }

        return symbols.get(symbol);
    }

    /**
     * @return The entries in this symbol table.
     */
    public Iterable<Info> getEntries() {
        return symbols.values();
    }

    /**
     * @return An alphabetically sorted copy of the entries in this symbol table.
     */
    public Iterable<Info> getEntriesSorted() {
        return new TreeSet<>(symbols.values());
    }

    @Override
    public String toString() {
        String header;
        if (isClass()) {
            header = "Class: " + getName();
            var this_ = (ClassInfo) lookup("this", false);
            if (this_ != null && this_.getParent() != null) {
                header += " extends " + this_.getParent().name;
            }
        } else if (isLocal()) {
            header = "Method: " + getName();
        } else {
            header = getName();
        }

        List<String> symbolNames = new ArrayList<>();
        List<String> symbolTypes = new ArrayList<>();
        List<String> returnTypes = new ArrayList<>();
        List<String> signatures = new ArrayList<>();
        symbolNames.add("Symbol");
        symbolTypes.add("Type");
        returnTypes.add("Return type");
        signatures.add("Signature");

        if (symbols.isEmpty()) {
            symbolNames.add("-");
            symbolTypes.add("-");
            returnTypes.add("-");
            signatures.add("-");
        }

        var entries = new TreeMap<>(symbols);
        entries.forEach((s, i) -> {
            symbolNames.add(s);
            if (i instanceof ClassInfo c) {
                symbolTypes.add("Class<" + c.name + ">");
                returnTypes.add("-");
                signatures.add("-");
            } else if (i instanceof MethodInfo m) {
                symbolTypes.add("Method");
                if (m.name.equals("#main")) {
                    returnTypes.add("void");
                    signatures.add("String[]");
                } else {
                    returnTypes.add(m.returnType.toString());
                    StringBuilder sig = new StringBuilder();
                    for (int j = 0; j < m.argumentCount(); j++) {
                        if (j != 0) sig.append(", ");
                        sig.append(m.getArgument(j));
                    }

                    if (m.argumentCount() == 0) sig.append("void");
                    signatures.add(sig.toString());
                }
            } else if (i instanceof VariableInfo v) {
                symbolTypes.add(v.type.toString());
                returnTypes.add("-");
                signatures.add("-");
            }
        });

        int maxSymbolLength = getMaxLength(symbolNames);
        int maxTypeLength = getMaxLength(symbolTypes);
        int maxReturnLength = getMaxLength(returnTypes);
        int maxSignatureLength = getMaxLength(signatures);

        int width = maxSymbolLength + maxTypeLength + maxReturnLength + maxSignatureLength + 3;

        StringBuilder sb = new StringBuilder();
        sb.append(header);

        sb.append(" ").append("-".repeat(width - header.length() - 2)).append("+");
        sb.append("\n");

        for (int i = 0; i < symbolNames.size(); i++) {
            if (i == 1) {
                sb.append("+").append("-".repeat(width - 2)).append("+\n");
            }
            sb.append("| ");
            String symbol = symbolNames.get(i);
            sb.append(symbol).append(" ".repeat(maxSymbolLength - symbol.length()));

            String type = symbolTypes.get(i);
            sb.append(type).append(" ".repeat(maxTypeLength - type.length()));

            String returnType = returnTypes.get(i);
            sb.append(returnType).append(" ".repeat(maxReturnLength - returnType.length()));

            String signature = signatures.get(i);
            sb.append(signature).append(" ".repeat(maxSignatureLength - signature.length()));

            sb.append("|\n");
        }

        sb.append("+").append("-".repeat(width - 2)).append("+\n");
        return sb.toString();
    }

    private int getMaxLength(Iterable<String> strings) {
        int length = 0;
        for (var str : strings) {
            length = Math.max(length, str.length());
        }
        return length + 5;
    }
}
