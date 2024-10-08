package semantics.type;

public class TypeBoolean extends TypeSingular {
    private static final TypeBoolean instance = new TypeBoolean();

    public static TypeBoolean getInstance() {
        return instance;
    }

    private TypeBoolean() {}

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
        return "boolean";
    }
}
