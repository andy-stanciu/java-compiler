class Main {
    public static void main(String[] args) {
        System.out.println("=== new Bar() ===");
        Bar bar = new Bar();
        System.out.println("=== new Bar(99) ===");
        bar = new Bar(99);
    }
}

class Foo {
    int x = 7;

    public Foo() {
        System.out.println("Foo() getValue()=" + getValue());
    }

    public int getValue() {
        return x;
    }
}

class Bar extends Foo {
    int extra = 42;

    public Bar() {
        this(0);
        System.out.println("Bar() extra=" + extra + " getValue()=" + getValue());
    }

    public Bar(int a) {
        System.out.println("Bar(int) before extra init: getValue()=" + getValue());
        System.out.println("Bar(int) extra=" + extra);
        extra = a == 0 ? 100 : a;
        System.out.println("Bar(int) after assign extra=" + extra);
    }

    public int getValue() {
        return extra;
    }
}