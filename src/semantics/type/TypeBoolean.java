package semantics.type;

public class TypeBoolean extends Type {
    private static final TypeBoolean instance = new TypeBoolean();

    public static TypeBoolean getInstance() {
        return instance;
    }

    private TypeBoolean() {}

    @Override
    public boolean isAssignableTo(Type other) {
        return this == other;
    }

    @Override
    public String toString() {
        return "boolean";
    }
}
