class Main {
    public static void main(String[] args) {
        Foo foo = new Foo();
        System.out.println("---");
        foo = new Foo(3);
    }
}

class Foo {
    int x = 0;
    int y = 0;

    public Foo() {
        this(10);
        System.out.println("Foo() x=" + x + " y=" + y);
    }

    public Foo(int a) {
        this(a * 10, (a + 1) * 10);
        System.out.println("Foo(int) x=" + x + " y=" + y);
    }

    public Foo(int a, int b) {
        x = a;
        y = b;
        System.out.println("Foo(int,int) x=" + x + " y=" + y);
    }
}