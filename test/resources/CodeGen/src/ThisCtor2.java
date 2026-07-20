class Main {
    public static void main(String[] args) {
        C c = new C();
        System.out.println("---");
        c = new C(1);
        System.out.println("---");
        c = new C(1, 2);
    }
}

class A {
    int x = initX();
    public int initX() { System.out.println("A.x init"); return 1; }

    public A() { this(0); System.out.println("A() x=" + x); }
    public A(int a) { System.out.println("A(int) x=" + x); x = a; }
}

class B extends A {
    int y = initY();
    public int initY() { System.out.println("B.y init"); return 2; }

    public B() { this(0); System.out.println("B() y=" + y); }
    public B(int a) { super(a); System.out.println("B(int) y=" + y); y = a; }
}

class C extends B {
    int z = initZ();
    public int initZ() { System.out.println("C.z init"); return 3; }

    public C() { this(0); System.out.println("C() z=" + z); }
    public C(int a) { this(a, 0); System.out.println("C(int) z=" + z); }
    public C(int a, int b) { super(a); System.out.println("C(int,int) z=" + z); z = b; }
}