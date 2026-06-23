class Main {
    public static void main(String[] args) {
        A a = new A();
        B b = new B();
        C c = new C();
        Baz baz;
        baz = new Baz(a, b, c);
        baz = new Baz(c, a, b);
        baz = new Baz(a, c, b);
        baz = new Baz(c, b, a);
        baz = new Baz(b, a, c);
        baz = new Baz(b, c, a);
        baz = new Baz(a, a, b);
        baz = new Baz(a, b, a);
        baz = new Baz(b, a, a);
        baz = new Baz(a, b, b);
        baz = new Baz(b, b, a);
        baz = new Baz(b, a, b);
        baz = new Baz(b, b, b);
        baz = new Baz(a, a, a);
        baz = new Baz(a, c, a);
        baz = new Baz(c, a, a);
        baz = new Baz(a, a, c);
        baz = new Baz(c, c, a);
        baz = new Baz(c, a, c);
        baz = new Baz(a, c, c);
        baz = new Baz(c, c, c);
        baz = new Baz(c, b, c);
        baz = new Baz(b, c, c);
        baz = new Baz(c, c, b);
        baz = new Baz(b, b, c);
        baz = new Baz(b, c, b);
        baz = new Baz(c, b, b);
    }
}

class A {
    public void print() {
        System.out.println("A");
    }
}
class B extends A {
    public void print() {
        System.out.println("B");
    }
}
class C extends B {
    public void print() {
        System.out.println("C");
    }
}

class Baz {
    public Baz(A a, A b, A c) {
        a.print();
        b.print();
        c.print();
    }
    public Baz(A a, B b, B c) {
        a.print();
        b.print();
        c.print();
    }
    public Baz(C a, B b, A c) {
        a.print();
        b.print();
        c.print();
    }
    public Baz(B a, C b, A c) {
        a.print();
        b.print();
        c.print();
    }
    public Baz(B a, B b, B c) {
        a.print();
        b.print();
        c.print();
    }
    public Baz(A a, C b, C c) {
        a.print();
        b.print();
        c.print();
    }
    public Baz(A a, B b, C c) {
        a.print();
        b.print();
        c.print();
    }
}