class Main {
    public static void main(String[] args) {
        {}
    }
}

class A extends B {}
class B extends A {}

class C extends C {}

class X extends W {
    int x;
}

class Y extends X {
    int y;
}

class Z extends Y {
    int z;
}

class W extends Y {
    int w;
}
