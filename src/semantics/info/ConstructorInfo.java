package semantics.info;

import lombok.Getter;
import semantics.table.SymbolTable;

public final class ConstructorInfo extends MemberInfo {
    @Getter
    private final Signature signature;
    public ConstructorInfo(SymbolTable parent, Signature signature) {
        super(parent, signature);
        this.signature = signature;
    }
}
