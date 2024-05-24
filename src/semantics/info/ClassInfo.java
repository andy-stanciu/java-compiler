package semantics.info;

import codegen.Generator;
import semantics.table.SymbolTable;
import semantics.table.TableType;

import java.util.*;

public final class ClassInfo extends Info {
    private final boolean main;
    private final SymbolTable table;
    private ClassInfo parent;
    private final Set<ClassInfo> children;
    private MethodInfo[] vtable;
    private int variableCount;

    public ClassInfo(SymbolTable parent, String name, boolean main) {
        super(name);
        this.table = new SymbolTable(parent, TableType.CLASS, name);
        this.children = new HashSet<>();
        this.main = main;
    }

    /**
     * Assigns instance variable offsets for this class.
     */
    public void assignVariableOffsets() {
        if (variableCount > 0) {
            throw new IllegalStateException("variable offsets already assigned");
        }

        // calculate variable count
        var curr = this;
        while (curr != null) {
            variableCount += curr.getTable().getVariableCount();
            curr = curr.parent;
        }

        // assign variable offsets
        int vIndex = variableCount - table.getVariableCount() + 1;
        for (var i : table.getEntriesSorted()) {
            if (i instanceof VariableInfo v) {
                if (v.vIndex == 0) {  // non-inherited instance variable
                    v.vIndex = vIndex;
                    vIndex++;
                }

            }
        }

        if (vIndex != variableCount + 1) {
            throw new IllegalStateException("failed to assign variable offsets");
        }
    }

    /**
     * Constructs the virtual table for this class.
     */
    public void constructVirtualTable() {
        if (isDerived() && parent.vtable == null) {
            throw new IllegalStateException("cannot construct vtable before parent");
        }

        if (vtable != null) {
            throw new IllegalStateException("vtable already constructed");
        } else {
            vtable = new MethodInfo[table.getMethodCount() + 1];
        }

        int offset = isDerived() ? parent.vtable.length : 1;

        for (var i : table.getEntriesSorted()) {
            if (i instanceof MethodInfo m) {
                if (m.overridden != null) {  // overrides another method
                    addMethod(m, m.overridden.vIndex);
                    m.vIndex = m.overridden.vIndex;
                } else if (m.vIndex != 0) {  // inherited method
                    addMethod(m, m.vIndex);
                } else {  // regular, non-inherited method
                    addMethod(m, offset);
                    m.vIndex = offset;
                    offset++;
                }
            }
        }
    }

    /**
     * @return All methods defined by this class.
     */
    public Iterator<MethodInfo> methodEntries() {
        var itr = Arrays.stream(vtable).iterator();
        itr.next(); // skip first (null) entry
        return itr;
    }

    /**
     * @return The size of an instance this class.
     */
    public int size() {
        return (variableCount + 1) * Generator.WORD_SIZE;
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

    /**
     * Adds the specified method to this class' virtual table.
     * @param m The method to add.
     * @param i The index into the virtual table.
     */
    private void addMethod(MethodInfo m, int i) {
        if (i >= vtable.length) {
            throw new IllegalArgumentException();
        }
        if (vtable[i] != null) {
            throw new IllegalStateException("vtable[" + i + "] already defined");
        }

        vtable[i] = m;
    }
}
