class Main {
    public static void main(String[] args) {
        C c = new C();
        System.out.println(c.x);
        System.out.println(c.y);
        System.out.println(c.z);
        System.out.println(c.w);
        System.out.println(c.v);
    }
}

class A {
    int x = 1;
    int y = x + 1;
}

class B extends A {
    int z = x + y;
}

class C extends B {
    int w = z + y;
    int v = w + x;
}