package semantics.type;

public abstract class Type {
    public abstract boolean isAssignableTo(Type other);
    public abstract boolean equals(Type other);
    public abstract String toString();

    public boolean isUndefined() {
        return this == TypeUndefined.getInstance();
    }

    public boolean isArray() {
        return isUndefined() || !(this instanceof TypeSingular);
    }
}
