package semantics.type;

import lombok.Getter;

public class TypeInt extends TypeSingular {
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
    public String toString() {
        return "int";
    }
}
