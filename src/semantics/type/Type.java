package semantics.type;

public abstract class Type {
    public abstract boolean isAssignableTo(Type other);
    public abstract boolean equals(Type other);
    public abstract String toString();

    public boolean isUnknown() {
        return this == TypeUnknown.getInstance();
    }

    public boolean isArray() {
        return isUnknown() || !(this instanceof TypeSingular);
    }
}
