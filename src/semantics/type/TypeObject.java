package semantics.type;

import semantics.info.ClassInfo;

public class TypeObject extends TypeSingular {
    public final ClassInfo base;  // base class (declared type)

    public TypeObject(ClassInfo base) {
        this.base = base;
    }

    @Override
    public boolean isAssignableTo(Type other) {
        if (other instanceof TypeObject obj) {
            if (base.isMain() || obj.base.isMain()) {
                return false;
            }

            var parent = base;
            while (parent.isDerived()) {
                if (parent == obj.base) {
                    return true;
                }
                parent = parent.getParent();
            }

            return parent == obj.base;
        }

        return other == TypeUnknown.getInstance();
    }

    @Override
    public boolean equals(Type other) {
        if (other instanceof TypeObject obj) {
            return base == obj.base;
        }

        return other == TypeUnknown.getInstance();
    }

    @Override
    public int getSimilarity(Type other) {
        if (other instanceof TypeObject obj) {
            if (base.isMain() || obj.base.isMain()) {
                return Integer.MAX_VALUE;
            }

            var parent = base;
            int similarity = 0;
            while (parent.isDerived()) {
                if (parent == obj.base) {
                    return similarity;
                }
                similarity++;
                parent = parent.getParent();
            }

            return parent == obj.base ? similarity : Integer.MAX_VALUE;
        }

        return other == TypeUnknown.getInstance() ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return base.name;
    }
}
