class Main {
    public static void main(String[] args) {
        C c = new C();
    }
}

class A {
    int x = 5;

    public A() {
        System.out.println("A constructor, x = " + x);
        x = 10;
        System.out.println("A constructor after assign, x = " + x);
    }
}

class B extends A {
    int y = x * 2;

    public B() {
        System.out.println("B constructor, x = " + x);
        System.out.println("B constructor, y = " + y);
        y = y + 1;
        System.out.println("B constructor after assign, y = " + y);
    }
}

class C extends B {
    int z = x + y;

    public C() {
        System.out.println("C constructor, x = " + x);
        System.out.println("C constructor, y = " + y);
        System.out.println("C constructor, z = " + z);
    }
}