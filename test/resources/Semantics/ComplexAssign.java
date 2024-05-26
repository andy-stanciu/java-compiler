class Main {
    public static void main(String[] args) {
        {}
    }
}

class A {
    B b;
}

class B {
    C c;
}

class C {
    int x;
}

class Assign {
    public int testAssign() {
        A a;
        a.b.c.x = 5;
        return 0;
    }

    public int testAssign2() {
        Container c;
        c.getFoo()[0] += 1;
        c.getFoo()[1] /= 7;
        if ((c.getFoo()[0] - 1 ^ c.getFoo()[2] / 2) == 0) {
            c.getFoo()[2] *= 5;
        } else {
            c.bar = false;
        }
        return 0;
    }
}

class Container {
    boolean bar;
    public int[] getFoo() {
        return new int[5];
    }
}

class Setter {
    int x;
    boolean y;
    A a;

    public int setX(int x) { this.x = x; return 0; }
    public int setY(boolean y) { this.y = y; return 0; }
    public int setA(A a) { this.a = a; return 0; }
}