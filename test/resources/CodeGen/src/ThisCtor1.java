class Main {
    public static void main(String[] args) {
        Foo foo = new Foo();
        System.out.println("---");
        foo = new Foo(5);
        System.out.println("---");
        foo = new Foo(3, 4);
    }
}

class Foo {
    int x = initX();
    int y = initY();

    public int initX() { System.out.println("Foo.x init"); return 1; }
    public int initY() { System.out.println("Foo.y init"); return 2; }

    public Foo() {
        this(10);
        System.out.println("Foo() x=" + x + " y=" + y);
    }

    public Foo(int a) {
        this(a, 99);
        System.out.println("Foo(int) x=" + x + " y=" + y);
    }

    public Foo(int a, int b) {
        System.out.println("Foo(int,int) x=" + x + " y=" + y);
        x = a;
        y = b;
        System.out.println("Foo(int,int) after assign x=" + x + " y=" + y);
    }
}