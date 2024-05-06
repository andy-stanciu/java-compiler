package semantics.table;

import semantics.info.*;

public final class SymbolContext {
    private static SymbolContext instance;

    public static SymbolContext getInstance() {
        if (instance == null) {
            instance = new SymbolContext();
        }
        return instance;
    }

    private final SymbolTable global;
    private SymbolTable table;

    private SymbolContext() {
        this.table = new SymbolTable();
        this.global = this.table;
    }

    /**
     * Enters a child symbol table with the specified name.
     * @param name The name of the table to enter.
     * @throws IllegalArgumentException If the child table does not exist.
     */
    public void enter(String name) {
        var info = lookup(name);

        if (info instanceof ClassInfo classInfo) {
            table = classInfo.getTable();
        } else if (info instanceof MethodInfo methodInfo) {
            table = methodInfo.getTable();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Exits the current symbol table, returning to the parent table.
     * @throws IllegalStateException If already in the global table.
     */
    public void exit() {
        if (!table.hasParent()) {
            throw new IllegalStateException("Already in global table");
        }

        table = table.getParent();
    }

    /**
     * Attempts to add a class entry to the symbol table in the current scope.
     * If it is already defined, reports an error.
     * @param name Name of the class.
     * @return The class entry that was added, or null if it couldn't be added.
     */
    public ClassInfo addClassEntry(String name) {
        return addClassEntry(name, false);
    }

    /**
     * Attempts to add a class entry to the symbol table in the current scope.
     * If it is already defined, reports an error.
     * @param name Name of the class.
     * @param main Whether the class is the main class.
     * @return The class entry that was added, or null if it couldn't be added.
     */
    public ClassInfo addClassEntry(String name, boolean main) {
        if (!table.isGlobal()) {
            throw new IllegalStateException("Cannot add class entry to current scope");
        }

        var class_ = new ClassInfo(table, main);
        if (main) {
            // define main method, special case
            class_.getTable().addEntry("#main", new MethodInfo(table));
        }

        return addEntry(name, class_) ? class_ : null;
    }

    /**
     * Attempts to add a method entry to the symbol table in the current scope.
     * If it is already defined, reports an error.
     * @param name Name of the method.
     * @return The method entry that was added, or null if it couldn't be added.
     */
    public MethodInfo addMethodEntry(String name) {
        if (!table.isClass()) {
            throw new IllegalStateException("Cannot add method entry to current scope");
        }

        var methodInfo = new MethodInfo(table);
        return addEntry(name, methodInfo) ? methodInfo : null;
    }

    /**
     * Attempts to add a variable entry to the symbol table in the current scope.
     * If it is already defined, reports an error.
     * @param name Name of the variable.
     * @return The variable entry that was added, or null if it couldn't be added.
     */
    public VariableInfo addVariableEntry(String name) {
        if (table.isGlobal()) {
            throw new IllegalStateException("Cannot add variable entry to current scope");
        }

        var variableInfo = new VariableInfo();
        return addEntry(name, variableInfo) ? variableInfo : null;
    }

    /**
     * Looks up the specified class within the global symbol table.
     * @param name The name of the class.
     * @return Information associated with the class, or null if the class
     *         is undefined or the main class.
     */
    public ClassInfo lookupClass(String name) {
        var result = global.lookup(name, true);
        if (result == null) return null;

        if (result instanceof ClassInfo classInfo && !classInfo.main) {
            return classInfo;
        }

        System.err.printf("Unexpected reference to main class \"%s\"%n", name);
        return null;
    }

    /**
     * Looks up a method within the specified class.
     * @param name The name of the method.
     * @param className The name of the class.
     * @return Information associated with the method, or null if
     *         the class does not define the method.
     */
    public MethodInfo lookupMethod(String name, String className) {
        if (name == null || className == null) return null;

        var class_ = lookupClass(className);
        if (class_ == null) return null;

        var result = class_.getTable().lookup(name, false);
        if (result instanceof MethodInfo methodInfo) {
            return methodInfo;
        }

        return null;
    }

    /**
     * Looks up a variable within the current scope. If not found,
     * recursively searches the parent scopes until it is found or
     * the global scope is reached.
     * @param name The name of the variable.
     * @return Information associated with the variable, or null if
     *         the variable is not defined.
     */
    public VariableInfo lookupVariable(String name) {
        if (name == null) return null;

        var result = lookup(name, true);
        if (result instanceof VariableInfo variableInfo) {
            return variableInfo;
        }

        return null;
    }

    /**
     * Attempts to add an entry to the symbol table in the current scope.
     * If it is already defined, reports an error.
     * @param symbol The symbol to add.
     * @param info Information associated with the symbol.
     * @return Whether the entry was added.
     */
    public boolean addEntry(String symbol, Info info) {
        if (!table.addEntry(symbol, info)) {
            System.err.printf("Symbol \"%s\" is already defined%n", symbol);
            return false;
        }
        return true;
    }

    /**
     * Looks up the specified symbol.
     * @param symbol The symbol to lookup.
     * @param searchParent If true, recursively searches the parent scopes until
     *                     the symbol is found or the global scope is reached.
     * @return Information associated with the symbol, or null if the symbol is undefined.
     */
    private Info lookup(String symbol, boolean searchParent) {
        if (!searchParent) {
            return table.lookup(symbol, true);
        }

        var curr = table;
        var result = curr.lookup(symbol, !curr.hasParent());
        while (result == null && curr.hasParent()) {
            curr = curr.getParent();
            result = curr.lookup(symbol, !curr.hasParent());
        }

        return result;
    }

    /**
     * Looks up the specified symbol within the current scope.
     * @param symbol The symbol to lookup.
     * @return Information associated with the symbol, or null if the symbol is undefined.
     */
    private Info lookup(String symbol) {
        return lookup(symbol, false);
    }
}
