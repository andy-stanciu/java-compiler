package semantics.table;

import semantics.IllegalSemanticException;
import semantics.Logger;
import semantics.info.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SymbolContext {
    public static SymbolContext create() {
        return new SymbolContext();
    }

    private final Logger logger;
    private final SymbolTable global;
    private SymbolTable table;
    private ClassInfo currentClass;

    private SymbolContext() {
        this.logger = Logger.getInstance();
        this.table = new SymbolTable();
        this.global = this.table;
    }

    /**
     * Dumps all symbol tables to stdout.
     */
    public void dump() {
        System.out.println(global);
        dumpTables(global.getEntriesSorted(), new HashSet<>());
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
            currentClass = classInfo;
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

        if (table.isClass()) currentClass = null;
        table = table.getParent();
    }

    /**
     * @return The {@link ClassInfo} that is currently in scope.
     * @throws IllegalStateException If no class is currently in scope.
     */
    public ClassInfo getCurrentClass() {
        if (currentClass == null) {
            throw new IllegalStateException();
        }

        return currentClass;
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

        var class_ = new ClassInfo(table, name, main);
        if (main) {
            // define main method, special case
            class_.getTable().addEntry("#main", new MethodInfo(class_.getTable(), "#main"));
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

        var methodInfo = new MethodInfo(table, name);
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

        var variableInfo = new VariableInfo(table, name);
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

        if (result instanceof ClassInfo classInfo && !classInfo.isMain()) {
            return classInfo;
        }

        logger.logError("Unexpected reference to main class \"%s\"%n", name);
        return null;
    }

    /**
     * Looks up a method within the specified class.
     * @param name The name of the method.
     * @param classInfo The class to perform the lookup within.
     * @return Information associated with the method, or null if
     *         the class does not define the method.
     */
    public MethodInfo lookupMethod(String name, ClassInfo classInfo) {
        if (name == null) return null;

        var result = classInfo.getTable().lookup(name, false);
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
            logger.logError("Symbol \"%s\" is already defined%n", symbol);
            return false;
        }
        return true;
    }

    /**
     * Checks whether the specified symbol is marked as undefined.
     * @param symbol The symbol to check.
     * @return Whether the symbol is undefined.
     */
    public boolean isUndefined(String symbol) {
        var curr = table;
        var result = curr.isUndefined(symbol);
        while (!result && curr.hasParent()) {
            curr = curr.getParent();
            result = curr.isUndefined(symbol);
        }

        return result;
    }

    /**
     * Propagates inherited members of all base classes to their derived classes.
     * Assumes the inheritance graph is acyclic.
     */
    public void propagateInheritedMembers() {
        Set<ClassInfo> baseClasses = new HashSet<>();
        Set<ClassInfo> derivedClasses = new HashSet<>();

        for (var entry : global.getEntries()) {
            ClassInfo class_ = (ClassInfo) entry;
            if (class_.isBase()) {
                baseClasses.add(class_);
            }
            if (class_.isDerived()) {
                derivedClasses.add(class_);
            }
        }

        while (!derivedClasses.isEmpty()) {
            int initialSize = derivedClasses.size();

            var itr = derivedClasses.iterator();
            while (itr.hasNext()) {
                var derived = itr.next();
                if (baseClasses.contains(derived.getParent())) {
                    itr.remove();  // remove from derived set
                    // class is directly derived from a base class, so we inherit its members
                    inheritMembers(derived);
                    baseClasses.add(derived);  // add to base set
                }
            }

            if (initialSize == derivedClasses.size()) {
                throw new IllegalSemanticException("unreachable");
            }
        }
    }

    /**
     * Inherits members from the direct superclass.
     * @param derived The derived class that should inherit its superclass' members.
     */
    private void inheritMembers(ClassInfo derived) {
        if (!derived.isDerived()) {
            throw new IllegalArgumentException("Expected derived class");
        }

        var base = derived.getParent();
        for (var entry : base.getTable().getEntries()) {
            if (entry instanceof MethodInfo method) {
                var overridingMethod = (MethodInfo)derived.getTable().lookup(method.name, false);
                if (overridingMethod == null) {
                    // Add base method to derived class
                    derived.getTable().addEntry(method.name, method);
                } else {
                    // Verify overriding method signature assignable to base method signature
                    if (!overridingMethod.returnType.isAssignableTo(method.returnType)) {
                        logger.logError("Expected return type of overriding method \"%s%s\" " +
                                "(%s) to be assignable to return type of \"%s%s\" (%s)%n",
                                derived.name, overridingMethod.name, overridingMethod.returnType,
                                base.name, method.name, method.returnType);
                    }

                    if (overridingMethod.argumentCount() != method.argumentCount()) {
                        logger.logError("Expected overriding method \"%s%s\" to have %d " +
                                "arguments, but got %d%n", derived.name,
                                overridingMethod.name, method.argumentCount(),
                                overridingMethod.argumentCount());
                    } else {
                        // argument counts match. verify that arguments are assignable
                        for (int i = 0; i < method.argumentCount(); i++) {
                            var baseType = method.getArgument(i);
                            var derivedType = overridingMethod.getArgument(i);
                            if (!derivedType.isAssignableTo(baseType)) {
                                logger.logError("Expected argument %d of overriding method " +
                                        "\"%s%s\" (%s) to be assignable to argument %d of " +
                                        "\"%s%s\" (%s)%n", i + 1, derived.name,
                                        overridingMethod.name, derivedType, i + 1, base.name,
                                        method.name, baseType);
                            }
                        }
                    }
                }
            } else if (entry instanceof VariableInfo variable) {
                // Inherit the instance variable. If it is already defined, will not overwrite
                derived.getTable().addEntry(variable.name, variable);
            }
        }
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

    /**
     * Recursive DFS to dump table entries to stdout.
     * @param entries Entries to dump.
     * @param visited The entries we have already visited.
     */
    private void dumpTables(Iterable<Info> entries, Set<Info> visited) {
        List<Info> children = new ArrayList<>();
        entries.forEach(entry -> {
            if (!visited.contains(entry)) {
                visited.add(entry);
                if (entry instanceof ClassInfo class_) {
                    System.out.println(class_.getTable());
                    class_.getTable().getEntriesSorted().forEach(e -> {
                        // add only methods to children
                        if (e instanceof MethodInfo) {
                            children.add(e);
                        }
                    });
                } else if (entry instanceof MethodInfo method) {
                    System.out.println(method.getTable());
                    method.getTable().getEntriesSorted().forEach(e -> {
                        // add only variables to children
                        if (e instanceof VariableInfo) {
                            children.add(e);
                        }
                    });
                }
            }
        });

        if (!children.isEmpty()) {
            dumpTables(children, visited);
        }
    }
}
