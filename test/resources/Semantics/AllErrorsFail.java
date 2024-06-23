class Main {
    public static void main(String[] args) {
        {}
    }
}

class Foo {
    public int getBar() { return 42; }
}

class Foo2 extends Foo {}

class A {
    int a;
    int [] arr2;

    public Foo getFoo() { return 0; }
    public int testIf() {
        if (5) {
            System.out.println(1);
        } else {
            System.out.println(2);
        }
        return 0;
    }

    public int testWhile() {
        Foo f;
        while (f) {
            a = f.getBar();
        }
        return 0;
    }

    public int testPrint() {
        Foo f;
        System.out.println(f);
        System.out.println(false);
        return 0;
    }

    public int assign(Foo f, Foo2 f2, int x) {
        int y;
        y = x;
        y = f;
        f2 = y;
        g = 2;
        return 0;
    }

    public int arrayAssign(int[] arr) {
        arr[false] = true;
        garbage[1] = 0;
        a[2] = 1;
        return 0;
    }

    public int unary() {
        int x;
        int y;
        x = -1;
        y = -true;
        x = +4;
        y = +false;
        return 0;
    }

    public int binaryOps() {
        Foo f;
        int x;
        int y;
        boolean a;
        boolean b;
        if (a && x) {
            a = x < b;
        } else {
            y = x + a;
        }
        b = x - a;
        arr2[0] = x * b;
        a = arr2[false];
        x = a[0];
        return 0;
    }

    public int misc(Foo2 f2) {
        int x;
        x = arr2.length;
        a = a.length;
        x = A;
        arr2 = new int[false];
        f2 = new x();
        if (!x) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        return 0;
    }

    public int callTest() {
        a = this.misc(new Foo2());
        a = this.misc(new Foo());
        a = this.misc(1);
        a = this.arrayAssign(1, new int[2]);
        a = this.assign(new Foo2(), new Foo2(), 1);
        a = this.assign(new Foo2(), new Foo(), 1);
        a = (1 + 1).misc(5);
        a = this.undefined();
        return a;
    }
}

class T {}
class T1 extends T {}
class S {}
class S1 extends S {}

class X {
    T t;
    S1 s;
    public T getT() { return t; }
    public S getS() { return s; }
    public int getA() { return 2; }
    public int getC() { return 0; }
    public int poly(T t, S1 s, int a, boolean b) { return 0; }
}

class Y extends X {
    T1 t1;
    S s;
    boolean getB;
    int[] getT;

    public T1 getT() { return t1; }
    public S1 getS() { return s; }
    public int getB() { return this.getA(); }
    public int getC(boolean fail) { return 0; }
    public int poly(T1 t, S s, int a, boolean b) { return 0; }
}