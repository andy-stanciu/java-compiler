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

            var parent = obj.base;
            while (parent.isDerived()) {
                if (parent == base) {
                    return true;
                }
                parent = parent.getParent();
            }

            return parent == base;
        }

        return false;
    }

    @Override
    public String toString() {
        return base.name;
    }
}
