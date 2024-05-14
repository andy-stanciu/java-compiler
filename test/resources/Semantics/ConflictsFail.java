class Main {
    public static void main(String[] args) {
        {}
    }
}

class A {}
class B {}
class A {}
class C {}
class B {}
class A extends C {}
class B extends C {}

class F {
    int x;
    boolean y;
    int[] x;

    public int getX() { return x; }
}

class M {
    public int foo() { return 0; }
    public int[] foo() { return new int[5]; }
    public F foo() { return new F(); }
    public F bar() { return new F(); }
    public boolean bar() { return true; }
}

class Arg {
    public int conflicts(F f, M m, int f) {
        return 0;
    }

    public int testConflicts() {
        return this.conflicts(new F(), new M(), 4);
    }
}