class Main {
    public static void main(String[] args) {
        C c = new C();
    }
}

class A {
    int x = initX();

    public int initX() {
        System.out.println("A.x init");
        return 1;
    }

    public A() {
        System.out.println("A constructor");
        System.out.println(x);
    }
}

class B extends A {
    int y = initY();

    public int initY() {
        System.out.println("B.y init");
        return 2;
    }

    public B() {
        System.out.println("B constructor");
        System.out.println(x);
        System.out.println(y);
    }
}

class C extends B {
    int z = initZ();

    public int initZ() {
        System.out.println("C.z init");
        return 3;
    }

    public C() {
        System.out.println("C constructor");
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
    }
}