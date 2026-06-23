class Main {
    public static void main(String[] args) {

    }
}

class A {
    int x;
    public A(int x) {
        super(2);
        this.x = x;
    }
}

class B extends A {
    public B() {
        int x = 0;
        super(3, 2);
    }
}

class C extends B {
    public C() {
        super(1);
        this.x = 4;
    }
}

class Baz {
    public Baz(int x, int y) {

    }
}

class Foo extends Baz {
    public Foo() {
        super(5, true);
    }
}

class Bar extends Foo {
    public Bar() {

    }

    public int doStuff() {
        super(1, false);
        return 5;
    }
}

class X {
    public X(Thing t1, Thing t2) {

    }
}

class Y extends X {
    public Y(Thing t1, ChildThing t2) {
        super(t1, t2);
    }

    public Y(ChildThing t1, Thing t2) {
        super(t1, t2);
    }

    public Y(ChildThing t1, ChildThing t2) {
        super(t1, t2);
    }

    public Y(ChildThing t1, ChildThing t2, boolean b) {
        super(t1, t2, true);
    }
}

class Thing {}
class ChildThing extends Thing {}

class F {
    public F(int[] arr) {

    }
}

class G extends F {
    int x;
    public G(int x) {
        this.x = x;
    }
}