class Main {
    public static void main(String[] args) {

    }
}

class A {
    public A() {
    }

    public A(int x) {

    }

    public A(int x, int y) {

    }

    public A(int x, int y, int z) {

    }
}

class B extends A {
    public B() {
        super();
    }

    public B(int x) {
        super(x);
    }

    public B(int x, int y) {
        super(x, y);
    }

    public B(int x, int y, int z) {
        super(x, y, z);
    }
}

class C extends B {
    boolean done;

    public C() {
        super(1);
    }

    public C(int x) {
        super(x, 2 * x);
        done = true;
    }

    public C(int x, int y, int z) {
        super(x);
        done = true;
    }

    public C(int x, int y, int z, int w) {
        super(x, y, z);
        if (w > 0) {
            done = true;
        }
    }

    public C(boolean done, int x, int y) {
        super(x, y);
        this.done = done;
    }
}

class D extends C {
    public D(int a, int b) {
        super(a, b, 4);
    }

    public D(int a, int b, int c) {
        super(false, a, b);
    }

    public D(int a, int b, int c, int d) {
        super(a, b, c, d);
    }
}