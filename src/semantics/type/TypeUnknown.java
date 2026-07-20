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
    public boolean comparableTo(Type other) {
        return true;
    }

    @Override
    public int getSimilarity(Type other) {
        return 0;
    }

    @Override
    public String toString() {
        return "unknown";
    }
}
