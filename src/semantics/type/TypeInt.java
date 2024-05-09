package semantics.type;

public class TypeInt extends Type {
    private static final TypeInt instance = new TypeInt();

    public static TypeInt getInstance() {
        return instance;
    }

    private TypeInt() {}

    @Override
    public boolean isAssignableTo(Type other) {
        return this == other || other == TypeUndefined.getInstance();
    }

    @Override
    public String toString() {
        return "int";
    }
}
