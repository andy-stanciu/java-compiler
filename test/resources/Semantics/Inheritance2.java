class Main {
    public static void main(String[] args) {
        {}
    }
}

class A {
    public int x() { return 0; }
    public int y() { return 0; }
}

class B extends A {
    public int x() { return 0; }
    public int z() { return 0; }
}

class C extends B {
    public int w() { return 0; }
}