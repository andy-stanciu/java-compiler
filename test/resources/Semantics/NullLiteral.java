class Main {
    public static void main(String[] args) {

    }
}

class Foo {
    A a = null;
    B b = null;
    A[] as = null;

    public Foo() {
        a = new A();
        b = null;
        as[0] = null;
        as = null;
        if (as[1] == null && null == null || b == null) {
            b = new B();
        }
    }

    public B getB() {
        String s = null;
        s += "foo";
        String t = "bar";
        t += null;
        String u = null + "foo" + null + "bar";
        return b == null ? new B() : null;
    }

    public A getA() {
        this.a = null;
        return null;
    }
}

class A {}
class B extends A {}