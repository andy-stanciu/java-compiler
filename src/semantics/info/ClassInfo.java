package semantics.info;

import semantics.table.SymbolTable;
import semantics.table.TableType;

import java.util.HashSet;
import java.util.Set;

public final class ClassInfo extends Info {
    private final boolean main;
    private final SymbolTable table;
    private ClassInfo parent;
    private final Set<ClassInfo> children;

    public ClassInfo(SymbolTable parentTable, String name, boolean main) {
        super(name);
        this.table = new SymbolTable(parentTable, TableType.CLASS, name);
        this.children = new HashSet<>();
        this.main = main;
    }

    public SymbolTable getTable() {
        return table;
    }

    /**
     * @return The parent of this class, or null if this class is a base class.
     */
    public ClassInfo getParent() {
        return parent;
    }

    /**
     * Sets the parent of this class.
     * @param parent The parent class.
     */
    public void setParent(ClassInfo parent) {
        this.parent = parent;
    }

    /**
     * @return The children of this class.
     */
    public Set<ClassInfo> getChildren() {
        return children;
    }

    /**
     * Adds a child to this class.
     * @param child The child class to add.
     */
    public void addChild(ClassInfo child) {
        if (!this.children.add(child)) {
            throw new IllegalStateException("Attempted to add child that was already present");
        }
    }

    /**
     * @return Whether this class is a base class (does not derive from any other class).
     */
    public boolean isBase() {
        return parent == null && !main;
    }

    /**
     * @return Whether this class is a derived class.
     */
    public boolean isDerived() {
        return parent != null && !main;
    }

    /**
     * @return Whether this class is the main class.
     */
    public boolean isMain() {
        return main;
    }
}
