package semantics.info;

import semantics.table.SymbolContext;

public abstract class Info implements Comparable<Info> {
    public final String name;

    public Info(String name) {
        this.name = name;
    }

    public Info(Signature signature) {
        this.name = SymbolContext.SIGNATURE_PREFIX + signature.toString();
    }

    @Override
    public int compareTo(Info other) {
        return name.compareTo(other.name);
    }
}
