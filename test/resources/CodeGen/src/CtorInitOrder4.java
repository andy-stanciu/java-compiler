class Main {
    public static void main(String[] args) {
        C c = new C();
        System.out.println(c.a1);
        System.out.println(c.a2);
        System.out.println(c.a3);
        System.out.println(c.b1);
        System.out.println(c.b2);
        System.out.println(c.c1);
        System.out.println(c.c2);
        System.out.println(c.c3);
    }
}

class A {
    int a1 = initA1();
    int a2 = initA2();
    int a3 = initA3();

    public int initA1() { System.out.println("A.a1"); return 1; }
    public int initA2() { System.out.println("A.a2"); return 2; }
    public int initA3() { System.out.println("A.a3"); return 3; }
}

class B extends A {
    int b1 = initB1();
    int b2 = initB2();

    public int initB1() { System.out.println("B.b1"); return 4; }
    public int initB2() { System.out.println("B.b2"); return 5; }
}

class C extends B {
    int c1 = initC1();
    int c2 = initC2();
    int c3 = initC3();

    public int initC1() { System.out.println("C.c1"); return 6; }
    public int initC2() { System.out.println("C.c2"); return 7; }
    public int initC3() { System.out.println("C.c3"); return 8; }
}