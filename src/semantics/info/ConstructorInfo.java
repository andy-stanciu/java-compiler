package semantics.info;

import lombok.Getter;
import semantics.table.SymbolTable;

public final class ConstructorInfo extends MemberInfo {
    // whether this constructor declaration explicitly invokes a super constructor
    public boolean invokesSuperCtor;
    // whether this constructor declaration *validly* explicitly invokes a super constructor
    public boolean validSuperCtor;
    // reference to the super constructor, or null if it doesn't exist
    public ConstructorInfo superCtor;

    // whether this constructor declaration explicitly invokes a "this" constructor
    public boolean invokesThisCtor;
    // whether this constructor declaration *validly* explicitly invokes a "this" constructor
    public boolean validThisCtor;
    // reference to the "this" constructor, or null if it doesn't exist
    public ConstructorInfo thisCtor;

    @Getter
    private final Signature signature;
    public ConstructorInfo(SymbolTable parent, Signature signature) {
        super(parent, signature);
        this.signature = signature;
    }
}
