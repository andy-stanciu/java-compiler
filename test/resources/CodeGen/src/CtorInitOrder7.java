class Main {
    public static void main(String[] args) {
        C c = new C();
        System.out.println(c.x);
        System.out.println(c.y);
        System.out.println(c.z);
    }
}

class A {
    int x = 5;

    public A() {
        System.out.println("A inline x = " + x);
        x = 10;
        System.out.println("A post-ctor x = " + x);
    }
}

class B extends A {
    int y = x * 2;

    public B() {
        System.out.println("B inline y = " + y);
        y = y + 1;
        System.out.println("B post-ctor y = " + y);
    }
}

class C extends B {
    int z = y + x;

    public C() {
        System.out.println("C inline z = " + z);
    }
}