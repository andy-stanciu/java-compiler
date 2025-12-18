package semantics.type;

public class TypeArray extends Type {
    public final TypeSingular type;
    public final int dimension;

    public TypeArray(TypeSingular type, int dimension) {
        this.type = type;
        this.dimension = dimension;
    }

    @Override
    public boolean isAssignableTo(Type other) {
        if (other instanceof TypeArray otherArray) {
            return dimension == otherArray.dimension &&
                    type.isAssignableTo(otherArray.type);
        }
        return other == TypeUnknown.getInstance();
    }

    @Override
    public boolean equals(Type other) {
        if (other instanceof TypeArray otherArray) {
            return dimension == otherArray.dimension &&
                    type.equals(otherArray.type);
        }
        return other == TypeUnknown.getInstance();
    }

    @Override
    public String toString() {
        return type + "[]".repeat(dimension);
    }
}
