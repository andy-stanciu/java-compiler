class Main {
    public static void main(String[] args) {
        C c = new C();
    }
}

class A {
    public A() {
        System.out.println("A constructor, getValue() = " + getValue());
    }

    public int getValue() {
        return -1;
    }
}

class B extends A {
    public B() {
        System.out.println("B constructor, getValue() = " + getValue());
    }

    public int getValue() {
        return -2;
    }
}

class C extends B {
    int val = 42;

    public C() {
        System.out.println("C constructor, val = " + val);
        System.out.println("C constructor, getValue() = " + getValue());
    }

    public int getValue() {
        return val;
    }
}