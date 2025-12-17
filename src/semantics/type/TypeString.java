package semantics.type;

public class TypeString extends TypeSingular {
    private static final TypeString instance = new TypeString();

    public static TypeString getInstance() {
        return instance;
    }

    private TypeString() {}

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
        return "String";
    }
}