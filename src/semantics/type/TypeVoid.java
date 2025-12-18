package semantics.type;

import lombok.Getter;

public class TypeVoid extends Type {
    @Getter
    private static final TypeVoid instance = new TypeVoid();

    private TypeVoid() {}

    @Override
    public boolean isAssignableTo(Type other) {
        return equals(other);
    }

    @Override
    public boolean equals(Type other) {
        return this == other || other == TypeUnknown.getInstance();
    }

    @Override
    public String toString() {
        return "void";
    }
}
