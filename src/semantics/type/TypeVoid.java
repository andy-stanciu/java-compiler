package semantics.type;

public class TypeVoid extends Type {
    private static final TypeVoid instance = new TypeVoid();

    public static TypeVoid getInstance() {
        return instance;
    }

    private TypeVoid() {}

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
        return "void";
    }
}
