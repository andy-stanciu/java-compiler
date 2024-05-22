package semantics.type;

import semantics.info.ClassInfo;

public class TypeObject extends Type {
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

        return other == TypeUndefined.getInstance();
    }

    @Override
    public boolean equals(Type other) {
        if (other instanceof TypeObject obj) {
            if (base.isMain() || obj.base.isMain()) {
                return false;
            }

            return base == obj.base;
        }

        return other == TypeUndefined.getInstance();
    }

    @Override
    public String toString() {
        return base.name;
    }
}
