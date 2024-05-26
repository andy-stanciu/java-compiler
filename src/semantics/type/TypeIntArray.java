package semantics.type;

public class TypeIntArray extends Type {
    private static final TypeIntArray instance = new TypeIntArray();

    public static TypeIntArray getInstance() {
        return instance;
    }

    private TypeIntArray() {}

    @Override
    public boolean isAssignableTo(Type other) {
        return equals(other);
    }

    @Override
    public boolean equals(Type other) {
        return this == other || other == TypeUndefined.getInstance();
    }

    @Override
    public String toString() {
        return "int[]";
    }
}
