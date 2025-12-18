package semantics.type;

import lombok.Getter;

public class TypeBoolean extends TypeSingular {
    @Getter
    private static final TypeBoolean instance = new TypeBoolean();

    private TypeBoolean() {}

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
        return "boolean";
    }
}
