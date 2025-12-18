package semantics.table;

import commons.Logger;
import semantics.info.*;
import semantics.type.TypeVoid;

import java.util.*;
import java.util.function.Consumer;

public final class SymbolContext {
    public static final String METHOD_PREFIX = "$";
    public static final String BLOCK_PREFIX = "#";

    public static SymbolContext create() {
        return new SymbolContext();
    }

    private final Logger logger;
    private final SymbolTable global;
    private final Stack<TableContext> contexts;
    private SymbolTable table;
    private ClassInfo currentClass;
    private MethodInfo currentMethod;
    private Stack<BlockInfo> currentBlocks;

    private SymbolContext() {
        this.logger = Logger.getInstance();
        this.table = new SymbolTable();
        this.global = this.table;
        this.contexts = new Stack<>();
        this.currentBlocks = new Stack<>();
    }

    /**
     * Swap the current symbol context with the specified class.
     * The current symbol context is saved, and can later be restored using
     * {@link SymbolContext#restore()}.
     * @param name The name of the class to swap with.
     */
    public void swap(String name) {
        contexts.push(new TableContext(table, currentClass, currentMethod, currentBlocks));
        table = global;
        currentClass = null;
        currentMethod = null;
        currentBlocks.clear();
        enterClass(name);
    }

    /**
     * Restores the current symbol context.
     * @throws IllegalStateException If there is no context to restore.
     */
    public void restore() {
        if (contexts.isEmpty()) {
            throw new IllegalStateException();
        }

        var context = contexts.pop();
        table = context.table();
        currentClass = context.currentClass();
        currentMethod = context.currentMethod();
        currentBlocks = context.currentBlocks();
    }

    /**
     * Dumps all symbol tables to stdout.
     */
    public void dump() {
        System.out.println(global);
        dumpTables(global.getEntriesSorted(), new HashSet<>());
    }

    /**
     * Enters a class with the specified name.
     * @param name The name of the class to enter.
     * @throws IllegalArgumentException If the class does not exist.
     */
    public void enterClass(String name) {
        var info = lookup(name);

        if (info instanceof ClassInfo classInfo) {
            table = classInfo.getTable();
            currentClass = classInfo;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Enters a method with the specified name.
     * @param name The name of the method to enter.
     * @throws IllegalArgumentException If the method does not exist.
     */
    public void enterMethod(String name) {
        var info = lookup(METHOD_PREFIX + name);

        if (info instanceof MethodInfo methodInfo) {
            table = methodInfo.getTable();
            currentMethod = methodInfo;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Enters the specified block.
     * @param block The block to enter.
     * @throws IllegalArgumentException If the block is null.
     */
    public void enterBlock(BlockInfo block) {
        if (block == null) {
            throw new IllegalStateException();
        }

        table = block.getTable();
        currentBlocks.push(block);
    }

    /**
     * Exits the current symbol table, returning to the parent table.
     * @throws IllegalStateException If already in the global table.
     */
    public void exit() {
        if (table.isGlobal()) {
            throw new IllegalStateException("Already in global table");
        } else if (table.isClass()) {
            currentClass = null;
        } else if (table.isLocal() && currentBlocks.empty()) {  // in method
            currentMethod = null;
        } else {  // in block
            currentBlocks.pop();
        }

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
     * @return The {@link MethodInfo} that is currently in scope.
     * @throws IllegalStateException If no method is currently in scope.
     */
    public MethodInfo getCurrentMethod() {
        if (currentMethod == null) {
            throw new IllegalStateException();
        }

        return currentMethod;
    }

    /**
     * @return Whether the symbol context has an {@link MethodInfo} in scope.
     */
    public boolean hasCurrentMethod() {
        return currentMethod != null;
    }

    /**
     * @return The {@link BlockInfo} that is currently in scope.
     * @throws IllegalStateException If no block is currently in scope.
     */
    public BlockInfo getCurrentBlock() {
        if (currentBlocks.isEmpty()) {
            throw new IllegalStateException();
        }

        return currentBlocks.peek();
    }

    /**
     * @return Whether a method is currently in scope.
     */
    public boolean isMethod() {
        return currentMethod != null;
    }

    /**
     * @return Whether a block is currently in scope.
     */
    public boolean isBlock() {
        return !currentBlocks.isEmpty();
    }

    /**
     * @return Whether a class is currently in scope.
     */
    public boolean isClass() {
        return currentClass != null;
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
            var mainMethod = new MethodInfo(class_.getTable(), "main");
            mainMethod.returnType = TypeVoid.getInstance();
            class_.getTable().addEntry(METHOD_PREFIX + "main", mainMethod);
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
        return addEntry(METHOD_PREFIX + name, methodInfo) ? methodInfo : null;
    }

    /**
     * Attempts to add a block entry to the symbol table in the current scope.
     * If it is already defined, reports an error.
     * @return The block entry that was added, or null if it couldn't be added.
     */
    public BlockInfo addBlockEntry() {
        if (!table.isLocal()) {
            throw new IllegalStateException("Cannot add block entry to current scope");
        }

        String name = String.valueOf(table.getBlockCount());
        var blockInfo = new BlockInfo(table, name);
        return addEntry(BLOCK_PREFIX + name, blockInfo) ? blockInfo : null;
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

        var variableInfo = new VariableInfo(table, name, table.isClass());
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

        var result = classInfo.getTable().lookup(METHOD_PREFIX + name, false);
        if (result instanceof MethodInfo methodInfo) {
            return methodInfo;
        }

        return null;
    }

    /**
     * Looks up a method within the current scope.
     * @param name The name of the method.
     * @return Information associated with the method, or null if
     *         the method is undefined.
     */
    public MethodInfo lookupMethod(String name) {
        return lookupMethod(name, getCurrentClass());
    }

    /**
     * Looks up an instance variable within the specified class.
     * @param name The name of the instance variable.
     * @param classInfo The class to perform the lookup within.
     * @return Information associated with the instance variable, or null if
     *         the class does not define the instance variable.
     */
    public VariableInfo lookupInstanceVariable(String name, ClassInfo classInfo) {
        if (name == null) return null;

        var result = classInfo.getTable().lookup(name, false);
        if (result instanceof VariableInfo variableInfo) {
            return variableInfo;
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
            if (symbol.startsWith(METHOD_PREFIX)) {
                symbol = symbol.substring(1);
            }
            logger.logError("Symbol \"%s\" is already defined%n", symbol);
            return false;
        }
        return true;
    }

    /**
     * Checks whether the specified symbol is marked as unknown.
     * @param symbol The symbol to check.
     * @return Whether the symbol is unknown.
     */
    public boolean isUnknown(String symbol) {
        var curr = table;
        var result = curr.isUndefined(symbol);
        while (!result && curr.hasParent()) {
            curr = curr.getParent();
            result = curr.isUndefined(symbol);
        }

        return result;
    }

    /**
     * Builds virtual tables of all classes in topological order.
     * Assumes the inheritance graph is acyclic.
     */
    public void buildVirtualTables() {
        visitClasses(this::buildVTable);
    }

    /**
     * Assigns variables offsets of all classes in topological order.
     * Assumes the inheritance graph is acyclic.
     */
    public void assignVariableOffsets() {
        visitClasses(this::assignVariableOffsets);
    }

    /**
     * Propagates inherited members of all base classes to their derived classes.
     * Assumes the inheritance graph is acyclic.
     */
    public void propagateInheritedMembers() {
        visitClasses(this::inheritMembers);
    }

    /**
     * Visits all classes in topological order. Applies the specified
     * action to each class.
     * @param action The action to apply to each class.
     */
    private void visitClasses(Consumer<ClassInfo> action) {
        Set<ClassInfo> baseClasses = new HashSet<>();
        Set<ClassInfo> derivedClasses = new HashSet<>();

        for (var entry : global.getEntries()) {
            ClassInfo class_ = (ClassInfo) entry;
            if (class_.isBase()) {
                baseClasses.add(class_);
                action.accept(class_);
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
                    action.accept(derived);
                    baseClasses.add(derived);  // add to base set
                }
            }

            if (initialSize == derivedClasses.size()) {
                throw new IllegalStateException("unreachable");
            }
        }
    }

    private void buildVTable(ClassInfo class_) {
        class_.constructVirtualTable();
    }

    private void assignVariableOffsets(ClassInfo class_) {
        class_.assignVariableOffsets();
    }

    /**
     * Inherits members from the direct superclass.
     * @param derived The derived class that should inherit its superclass' members.
     */
    private void inheritMembers(ClassInfo derived) {
        if (derived.isBase()) {  // nothing to do if the class is a base class
            return;
        }

        var base = derived.getParent();
        for (var entry : base.getTable().getEntries()) {
            if (entry instanceof MethodInfo method) {
                var overridingMethod = (MethodInfo)derived.getTable().lookup(
                        METHOD_PREFIX + method.name, false);
                if (overridingMethod == null) {
                    // Add base method to derived class
                    derived.getTable().addEntry(METHOD_PREFIX + method.name, method);
                } else {
                    overridingMethod.overridden = method;
                    // Verify overriding method signature assignable to base method signature
                    if (!overridingMethod.returnType.isAssignableTo(method.returnType)) {
                        logger.logError("Expected return type of overriding method \"%s$%s\" " +
                                "(%s) to be assignable to return type of \"%s$%s\" (%s)%n",
                                derived.name, overridingMethod.name, overridingMethod.returnType,
                                base.name, method.name, method.returnType);
                    }

                    if (overridingMethod.argumentCount() != method.argumentCount()) {
                        logger.logError("Expected overriding method \"%s$%s\" to have %d " +
                                "arguments, but got %d%n", derived.name,
                                overridingMethod.name, method.argumentCount(),
                                overridingMethod.argumentCount());
                    } else {
                        // argument counts match. verify that argument types are equal
                        for (int i = 0; i < method.argumentCount(); i++) {
                            var baseType = method.getArgument(i);
                            var derivedType = overridingMethod.getArgument(i);
                            if (!derivedType.equals(baseType)) {
                                logger.logError("Expected argument %d of overriding method " +
                                        "\"%s$%s\" (%s) to be equal to argument %d of " +
                                        "\"%s$%s\" (%s)%n", i + 1, derived.name,
                                        overridingMethod.name, derivedType, i + 1, base.name,
                                        method.name, baseType);
                            }
                        }
                    }
                }
            } else if (entry instanceof VariableInfo variable) {
                // Inherit the instance variable. If it is already defined, will not overwrite
                // We inherit it as a "transient", i.e. we don't increment the # of instance
                // variables declared by the derived class.
                derived.getTable().addEntry(variable.name, variable, true);
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
                        // add variables and blocks to children
                        if (e instanceof VariableInfo || e instanceof BlockInfo) {
                            children.add(e);
                        }
                    });
                } else if (entry instanceof BlockInfo block) {
                    System.out.println(block.getTable());
                    block.getTable().getEntriesSorted().forEach(e -> {
                        // add variables and blocks to children
                        if (e instanceof VariableInfo || e instanceof BlockInfo) {
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
