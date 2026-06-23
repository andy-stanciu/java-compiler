class Main {
    public static void main(String[] args) {
        C c = new C();
        System.out.println(c.x);
        System.out.println(c.y);
        System.out.println(c.z);
    }
}

class A {
    int x = 10;
}

class B extends A {
    int y = x + 5;
}

class C extends B {
    int z = x + y;
}