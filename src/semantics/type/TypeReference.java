package semantics.type;

public class TypeReference extends Type {
    public final String name;

    public TypeReference(String name) {
        this.name = name;
    }

    @Override
    public boolean isAssignableTo(Type other) {
        // TODO: handling subclassing here
        return other instanceof TypeReference;
    }

    @Override
    public String toString() {
        return name;
    }
}
