class Main {
    public static void main(String[] args) {
        Foo[] foos = new Foo[10];
        for (int i = 0; i < foos.length; i++) {
            if (i % 2 == 0) {
                foos[i] = new Foo(i, i);
            } else {
                foos[i] = new Bar(i > 5);
            }
        }
        for (int i = 0; i < foos.length; i++) {
            for (int j = 0; j < foos[i].arr.length; j++) {
                System.out.println(foos[i].a);
                System.out.println(foos[i].arr[j].x);
                System.out.println(foos[i].arr[j].y);
            }
        }
    }
}

class Foo {
    int a = 0;
    A[] arr = createArr(a);

    public Foo() {
        System.out.println("Foo constructor 1");
        System.out.println(a);
        System.out.println(arr.length);
    }

    public Foo(int a, int b) {
        System.out.println("Foo constructor 2");
        System.out.println(a);
        System.out.println(arr.length);
        if (arr[0].x == 2) {
            arr[1] = new B();
        } else {
            arr[2] = new B();
        }
        a += 10;
    }

    public A[] createArr(int a) {
        A[] ret = new A[4];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new A(i % 2 == 0);
            ret[i].x += a;
            System.out.println(ret[i].x);
        }
        return ret;
    }
}

class Bar extends Foo {
    public Bar(boolean b) {
        System.out.println("Bar constructor");
        System.out.println(a);
        System.out.println(arr.length);
        if (b) {
            this.arr = createArr(3);
            this.a *= 2;
        } else {
            this.arr = createArr(5);
        }
    }
}

class A {
    int x = 2;
    int y = 3;
    int z = doThing();

    public A() {
        System.out.println("A constructor 1");
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
    }

    public A(boolean mult) {
        System.out.println("A constructor 2");
        System.out.println(mult);
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
        if (mult) {
            x *= 2;
            y *= 2;
        } else {
            x += 5;
            y += 5;
        }
    }

    public int doThing() {
        return 17;
    }
}

class B extends A {
    String s = "<";
    String e = ">";
    public B() {
        System.out.println("B constructor");
        System.out.println(s);
        System.out.println(e);
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
        s = this.x == 2 ? s + this.e : s + s;
        e = this.y == 3 ? e + this.e : e + s;
    }

    public int doThing() {
        return 42;
    }
}