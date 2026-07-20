package semantics.type;

import lombok.Getter;

public class TypeNull extends Type {
    @Getter
    private static final TypeNull instance = new TypeNull();

    private TypeNull() {}

    @Override
    public boolean isAssignableTo(Type other) {
        return !(other instanceof TypePrimitive);
    }

    @Override
    public boolean equals(Type other) {
        return this == other || other == TypeUnknown.getInstance();
    }

    @Override
    public boolean comparableTo(Type other) {
        return isAssignableTo(other);
    }

    @Override
    public int getSimilarity(Type other) {
        return isAssignableTo(other) ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "null";
    }
}
