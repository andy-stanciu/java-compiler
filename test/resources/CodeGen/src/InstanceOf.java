class Test {
    public static void main(String[] args) {
        System.out.println(new Driver().run());
    }
}

class Driver {
    public int run() {
        A a1;
        A a2;
        A a3;
        C c1;
        C c2;
        F f1;
        G2 g2;

        a1 = new A();
        a2 = new B();
        a3 = new D();
        c1 = new C();
        c2 = new D();
        f1 = new G();
        g2 = new G2();

        if (a1 instanceof A) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        if (a2 instanceof B) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        if (a3 instanceof C) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        if (c1 instanceof C) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        if (c2 instanceof D) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        if (f1 instanceof G) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        if (g2 instanceof G2) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        if (a1 instanceof C) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        return 0;
    }
}

class A { int x; }
class B extends A { int b; }
class C extends B { boolean y; }
class D extends C {}

class F {}
class G extends F { int[] r; }
class G2 extends F {}