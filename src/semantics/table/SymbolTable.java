package semantics.table;

import semantics.Logger;
import semantics.info.*;

import java.util.*;

public final class SymbolTable {
    private final SymbolTable parent;
    private final Logger logger;
    private final Map<String, Info> symbols;
    private final Set<String> undefined;
    private final TableType type;
    private final String name;
    private int classCount;
    private int methodCount;
    private int blockCount;
    private int variableCount;

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
        this.undefined = new HashSet<>();
        this.logger = Logger.getInstance();
        this.parent = parent;
        this.type = type;
        this.name = name;
    }

    /**
     * @return The size (number of entries) in this symbol table.
     */
    public int size() {
        return symbols.size();
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
     * @return Whether the specified symbol is marked as undefined in this symbol table.
     */
    public boolean isUndefined(String symbol) {
        return undefined.contains(symbol);
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
        return addEntry(symbol, info, false);
    }

    /**
     * Adds the specified entry to this symbol table.
     *
     * @param symbol      Symbol associated with the entry.
     * @param info        Information associated with the symbol.
     * @param isTransient Whether the entry is transient. A transient entry does not
     *                    contribute to its reference count.
     * @return Whether the entry was successfully added.
     */
    public boolean addEntry(String symbol, Info info, boolean isTransient) {
        if (symbol == null) return false;

        if (symbols.containsKey(symbol)) {
            return false;
        }

        undefined.remove(symbol);  // remove from undefined set
        symbols.put(symbol, info);

        if (!isTransient) {
            if (info instanceof ClassInfo) {
                classCount++;
            } else if (info instanceof MethodInfo) {
                methodCount++;
            } else if (info instanceof BlockInfo) {
                blockCount++;
            } else if (info instanceof VariableInfo) {
                variableCount++;
            }
        }

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
        if (report && !symbols.containsKey(symbol) && !undefined.contains(symbol)) {
            logger.logError("Undefined symbol \"%s\"%n", symbol);
            undefined.add(symbol); // mark as undefined
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

    /**
     * @return The number of methods defined in this symbol table.
     */
    public int getMethodCount() {
        return methodCount;
    }

    /**
     * @return The number of blocks defined in this symbol table.
     */
    public int getBlockCount() {
        return blockCount;
    }

    /**
     * @return The number of variables defined in this symbol table.
     */
    public int getVariableCount() {
        return variableCount;
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
        } else if (isLocal() && parent.isClass()) {
            header = "Method: " + getName();
        } else if (isLocal() && parent.isLocal()) {
            header = "Block: " + getName();
        } else {
            header = getName();
        }

        List<String> symbolNames = new ArrayList<>();
        List<String> symbolTypes = new ArrayList<>();
        List<String> returnTypes = new ArrayList<>();
        List<String> signatures = new ArrayList<>();
        List<String> inherited = new ArrayList<>();
        symbolNames.add("Symbol");
        symbolTypes.add("Type");
        returnTypes.add("Return");
        signatures.add("Signature");
        inherited.add("Inherited");

        if (symbols.isEmpty()) {
            symbolNames.add("-");
            symbolTypes.add("-");
            returnTypes.add("-");
            signatures.add("-");
            inherited.add("-");
        }

        var entries = new TreeMap<>(symbols);
        entries.forEach((s, i) -> {
            if (s.startsWith(SymbolContext.METHOD_PREFIX)) {
                s = s.substring(1);
            }
            symbolNames.add(s);

            if (i instanceof ClassInfo c) {
                symbolTypes.add("Class<" + c.name + ">");
                returnTypes.add("-");
                signatures.add("-");
                inherited.add("-");
            } else if (i instanceof MethodInfo m) {
                symbolTypes.add("Method");
                if (m.name.equals("main")) {
                    returnTypes.add("void");
                    signatures.add("String[]");
                    inherited.add("-");
                } else {
                    returnTypes.add(m.returnType.toString());
                    StringBuilder sig = new StringBuilder();
                    for (int j = 0; j < m.argumentCount(); j++) {
                        if (j != 0) sig.append(", ");
                        sig.append(m.getArgument(j));
                    }

                    if (m.argumentCount() == 0) sig.append("void");
                    signatures.add(sig.toString());

                    String origin = m.getTable().getParent().getName();
                    if (!origin.equals(getName())) {
                        inherited.add(origin);
                    } else {
                        inherited.add("-");
                    }
                }
            } else if (i instanceof BlockInfo b) {
                symbolTypes.add("Block");
                returnTypes.add("-");
                signatures.add("-");

                String origin = b.getTable().getParent().getName();
                if (!origin.equals(getName())) {
                    inherited.add(origin);
                } else {
                    inherited.add("-");
                }
            } else if (i instanceof VariableInfo v) {
                symbolTypes.add(v.type.toString());
                returnTypes.add("-");
                signatures.add("-");

                String origin = v.getParent().getName();
                if (!origin.equals(getName())) {
                    inherited.add(origin);
                } else {
                    inherited.add("-");
                }
            }
        });

        int maxSymbolLength = getMaxLength(symbolNames);
        int maxTypeLength = getMaxLength(symbolTypes);
        int maxReturnLength = getMaxLength(returnTypes);
        int maxSignatureLength = getMaxLength(signatures);
        int maxInheritedLength = getMaxLength(inherited);

        int width = maxSymbolLength + maxTypeLength + maxReturnLength +
                maxSignatureLength + maxInheritedLength;
        int buffer = Math.max(0, header.length() - width);
        width = Math.max(width, header.length()) + 3;

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

            String inheritedFrom = inherited.get(i);
            sb.append(inheritedFrom).append(" ".repeat(maxInheritedLength - inheritedFrom.length() + buffer));

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
