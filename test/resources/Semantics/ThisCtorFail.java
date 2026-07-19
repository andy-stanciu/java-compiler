class Main {
    public static void main(String[] args) {

    }
}

class A {
    int x;
    public A() {
        this(0);
    }
    public A(int x) {
        this.x = x;
    }
}

class B extends A {
    public B() {
        this(1, 1);
    }

    public B(int x) {
        super(x);
        this(0);
    }
}

class Foo {
    public Foo() {
        this();
    }
}

class Bar {
    public Bar() {
        this(1);
    }

    public Bar(boolean b) {
        this();
        int a = 0;
    }

    public void doStuff() {
        this();
    }
}

class Baz {
    public Baz() {
        this(1);
    }

    public Baz(int x, int y, int z) {
        this();
    }

    public Baz(int x) {
        this(1, 2);
    }

    public Baz(int x, int y) {
        this(1, 2, 3);
    }
}

class X {
    public X() {
        this(1);
    }

    public X(int x) {
        this(1);
    }
}

class Y {
    public Y() {
        this();
    }

    public Y() {
        this();
    }

    public Y(int x) {
        this(0);
    }

    public Y(int[] a) {
        this(a.length);
    }

    public Y(int[] a, int[] b, int c) {

    }

    public Y(int[] b) {

    }

    public Y(int[] a, int[] b, int c) {

    }
}

class Z {
    public Z() {
        this();
    }

    public Z(int x) {
        this(1, 2);
    }

    public Z(int x, int y) {
        this(1);
    }

    public Z(int x, int y, int z) {
        this(1);
    }
}