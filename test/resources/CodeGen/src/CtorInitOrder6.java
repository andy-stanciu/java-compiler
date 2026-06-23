class Main {
    public static void main(String[] args) {
        C c = new C();
        System.out.println(c.x);
        System.out.println(c.y);
        System.out.println(c.z);
        System.out.println(c.w);
    }
}

class A {
    int x = 2;
    int y = x * 3;
}

class B extends A {
    int z = y + 10;
}

class C extends B {
    int w = z * 2;
}