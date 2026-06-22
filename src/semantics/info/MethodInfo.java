package semantics.info;

import codegen.Generator;
import semantics.table.SymbolTable;
import semantics.type.Type;

public final class MethodInfo extends MemberInfo {
    public Type returnType;
    public int vIndex;  // index into vtable
    public MethodInfo overridden;  // pointer to overridden method, or null if not overriding

    public MethodInfo(SymbolTable parent, String name) {
        super(parent, name);
    }

    /**
     * @return The offset in the vtable.
     */
    public int getOffset() {
        if (vIndex == 0) {
            throw new IllegalStateException();
        }

        return vIndex * Generator.WORD_SIZE;
    }
}
