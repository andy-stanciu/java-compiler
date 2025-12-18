package semantics.type;

import lombok.Getter;

public class TypeUnknown extends TypeSingular {
    @Getter
    private static final TypeUnknown instance = new TypeUnknown();

    private TypeUnknown() {}

    @Override
    public boolean isAssignableTo(Type other) {
        return true;
    }

    @Override
    public boolean equals(Type other) {
        return true;
    }

    @Override
    public String toString() {
        return "unknown";
    }
}
