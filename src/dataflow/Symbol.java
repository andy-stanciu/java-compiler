package dataflow;

public record Symbol(String name, int lineNumber) {
    public static Symbol fromName(String name) {
        return new Symbol(name, -1);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Symbol s && name.equals(s.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
