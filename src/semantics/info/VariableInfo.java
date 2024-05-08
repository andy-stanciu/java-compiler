package semantics.info;

import semantics.type.Type;

public final class VariableInfo extends Info {
    public Type type;

    public VariableInfo(String name) {
        super(name);
    }
}
