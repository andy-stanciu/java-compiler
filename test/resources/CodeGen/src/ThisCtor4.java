class Main {
    public static void main(String[] args) {
        Bar bar = new Bar(true);
        System.out.println("---");
        bar = new Bar(false);
    }
}

class Foo {
    int x = 5;

    public Foo() {
        System.out.println("Foo() x=" + x);
        x = 20;
        System.out.println("Foo() after assign x=" + x);
    }

    public Foo(int a) {
        System.out.println("Foo(int) x=" + x);
        x = a;
        System.out.println("Foo(int) after assign x=" + x);
    }
}

class Bar extends Foo {
    int y = x * 2;

    public Bar(boolean flag) {
        this(flag ? 1 : 2);
        System.out.println("Bar(boolean) x=" + x + " y=" + y);
    }

    public Bar(int a) {
        super(a);
        System.out.println("Bar(int) x=" + x + " y=" + y);
        y = x + 10;
        System.out.println("Bar(int) after assign x=" + x + " y=" + y);
    }
}