package semantics.type;

public class TypeInt extends TypeSingular {
    private static final TypeInt instance = new TypeInt();

    public static TypeInt getInstance() {
        return instance;
    }

    private TypeInt() {}

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
        return "int";
    }
}
