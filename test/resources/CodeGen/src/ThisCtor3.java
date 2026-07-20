class Main {
    public static void main(String[] args) {
        Foo foo = new Foo();
    }
}

class Foo {
    int x = initX();
    public int initX() { System.out.println("Foo.x init"); return 10; }

    public Foo() {
        this(1);
        System.out.println("Foo() x=" + x);
    }

    public Foo(int a) {
        this(a, 2);
        System.out.println("Foo(int) x=" + x);
    }

    public Foo(int a, int b) {
        this(a, b, 3);
        System.out.println("Foo(int,int) x=" + x);
    }

    public Foo(int a, int b, int c) {
        System.out.println("Foo(int,int,int) x=" + x);
        x = a + b + c;
        System.out.println("Foo(int,int,int) after assign x=" + x);
    }
}