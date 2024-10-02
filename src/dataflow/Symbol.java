package dataflow;

public record Symbol(String name, int lineNumber) {
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
