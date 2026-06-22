class Main {
    public static void main(String[] args) {
        Foo f = new Foo(1, 2, false);
        f.print();
        f = new Foo(4, 5, true);
        f.print();
    }
}

class Foo {
    int x;
    int y;
    boolean z;

    public Foo(int x, int y, boolean z) {
        System.out.println(this.x);
        System.out.println(this.y);
        System.out.println(this.z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void print() {
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
    }
}