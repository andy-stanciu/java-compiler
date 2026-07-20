package semantics.type;

import lombok.Getter;

public class TypeInt extends TypePrimitive {
    @Getter
    private static final TypeInt instance = new TypeInt();

    private TypeInt() {}

    @Override
    public boolean isAssignableTo(Type other) {
        return equals(other);
    }

    @Override
    public boolean equals(Type other) {
        return this == other || other == TypeUnknown.getInstance();
    }

    @Override
    public boolean comparableTo(Type other) {
        return equals(other);
    }

    @Override
    public int getSimilarity(Type other) {
        return isAssignableTo(other) ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "int";
    }
}
