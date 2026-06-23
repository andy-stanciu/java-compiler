package semantics.info;

import lombok.Getter;
import semantics.table.SymbolContext;
import semantics.table.SymbolTable;
import semantics.table.TableType;
import semantics.type.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class MemberInfo extends Info {
    public int lineNumber;
    public int endLineNumber;
    private final List<Type> argumentTypes;
    private final Set<String> argumentNames;
    private final List<VariableInfo> localVariables;
    public int frameSize;  // frame size of member

    @Getter
    private final SymbolTable table;

    public MemberInfo(SymbolTable parent, String name) {
        super(name);
        this.table = new SymbolTable(parent, TableType.LOCAL, parent.getName() +
                SymbolContext.METHOD_PREFIX + name);
        this.argumentTypes = new ArrayList<>();
        this.argumentNames = new HashSet<>();
        this.localVariables = new ArrayList<>();
    }

    public MemberInfo(SymbolTable parent, Signature signature) {
        super(signature);
        this.table = new SymbolTable(parent, TableType.LOCAL, signature.getFullName());
        this.argumentTypes = new ArrayList<>();
        this.argumentNames = new HashSet<>();
        this.localVariables = new ArrayList<>();
    }

    /**
     * @return The qualified member name.
     */
    public String getQualifiedName() {
        return table.getName();
    }

    public int argumentCount() {
        return argumentTypes.size();
    }

    public void addArgument(String name, Type type) {
        argumentTypes.add(type);
        argumentNames.add(name);
    }

    public Type getArgument(int i) {
        return argumentTypes.get(i);
    }

    public Iterable<String> getArgumentNames() {
        return argumentNames;
    }

    public void addLocalVariable(VariableInfo v) {
        localVariables.add(v);
    }

    public VariableInfo getLocalVariable(int i) {
        return localVariables.get(i);
    }

    public int localVariableCount() {
        return localVariables.size();
    }
}
