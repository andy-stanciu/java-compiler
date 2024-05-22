package semantics.type;

public class TypeUndefined extends Type {
    private static final TypeUndefined instance = new TypeUndefined();

    public static TypeUndefined getInstance() {
        return instance;
    }

    private TypeUndefined() {}

    @Override
    public boolean isAssignableTo(Type other) {
        return true;
    }

    @Override
    public boolean equals(Type other) {
        return true;
    }

    @Override
    public String toString() {
        return "undefined";
    }
}
