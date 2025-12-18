package commons;

public final class Indenter {
    private final int indentSize;
    private int indent;

    public static Indenter create() {
        return new Indenter();
    }

    public static Indenter create(int indentSize) {
        return new Indenter(indentSize);
    }

    private Indenter() {
        this(2);
    }

    private Indenter(int indentSize) {
        if (indentSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.indentSize = indentSize;
        this.indent = 0;
    }

    public void print() {
        System.out.print(" ".repeat(indent));
    }

    public void push() {
        indent += indentSize;
    }

    public void pop() {
        indent -= indentSize;
        if (indent < 0) {
            throw new IllegalStateException();
        }
    }
}
