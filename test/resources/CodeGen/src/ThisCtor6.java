class Main {
    public static void main(String[] args) {
        System.out.println("=== new C() ===");
        C c = new C();
        System.out.println("=== new C(1) ===");
        c = new C(1);
        System.out.println("=== new C(1,2) ===");
        c = new C(1, 2);
        System.out.println("=== new C(1,2,3) ===");
        c = new C(1, 2, 3);
    }
}

class A {
    int a = initA();
    public int initA() { System.out.println("A.a init"); return 1; }

    public A() {
        this(0);
        System.out.println("A() a=" + a);
    }
    public A(int x) {
        System.out.println("A(int) a=" + a);
        a = x;
    }
}

class B extends A {
    int b = initB();
    public int initB() { System.out.println("B.b init"); return 2; }

    public B() {
        this(0);
        System.out.println("B() b=" + b);
    }
    public B(int x) {
        this(x, 0);
        System.out.println("B(int) b=" + b);
    }
    public B(int x, int y) {
        super(x);
        System.out.println("B(int,int) b=" + b);
        b = y;
    }
}

class C extends B {
    int c = initC();
    public int initC() { System.out.println("C.c init"); return 3; }

    public C() {
        this(0);
        System.out.println("C() c=" + c);
    }
    public C(int x) {
        this(x, 0);
        System.out.println("C(int) c=" + c);
    }
    public C(int x, int y) {
        this(x, y, 0);
        System.out.println("C(int,int) c=" + c);
    }
    public C(int x, int y, int z) {
        super(x, y);
        System.out.println("C(int,int,int) c=" + c);
        c = z;
    }
}