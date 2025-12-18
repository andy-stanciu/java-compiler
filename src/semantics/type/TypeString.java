package semantics.type;

import lombok.Getter;

public class TypeString extends TypeSingular {
    @Getter
    private static final TypeString instance = new TypeString();

    private TypeString() {}

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
        return "String";
    }
}