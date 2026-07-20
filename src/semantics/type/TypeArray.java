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
    public boolean comparableTo(Type other) {
        return other == TypeNull.getInstance() || equals(other);
    }

    @Override
    public int getSimilarity(Type other) {
        if (other instanceof TypeArray otherArray && dimension == otherArray.dimension) {
            return type.getSimilarity(otherArray.type);
        }
        return other == TypeUnknown.getInstance() ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return type + "[]".repeat(dimension);
    }
}
