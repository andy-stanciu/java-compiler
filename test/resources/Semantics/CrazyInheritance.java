class Main {
    public static void main(String[] args) {
        {}
    }
}

class A {
    int a;

    public int aaa(A a) { return 0; }
}
class B extends A {}
class C extends B {
    int c;
}
class D extends C {}
class E extends D {
    public int eee(E e) { return 0; }
}
class F extends E {}
class G extends F {}
class H extends G {
    boolean h;
}
class I extends H {}
class J extends I {}
class K extends J {}
class L extends K {}
class M extends L {
    public int mmm(M m) { return 0; }
}
class N extends M {}
class O extends N {}
class P extends O {
    int[] p;
}
class Q extends P {}
class R extends Q {}
class S extends R {}
class T extends S {
    int t;
}
class U extends T {}
class V extends U {}
class W extends V {}
class X extends W {
    int x;
}
class Y extends X {}
class Z extends Y {}

