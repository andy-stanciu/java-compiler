class Main {
    public static void main(String[] args) {
        System.out.println(new Foo().getA()[0]);
        System.out.println(new Foo().getA()[1]);
    }
}

class Foo {
    int[] a;
    public int[] getA() {
        return a;
    }
    public Foo() {
        a = new int[2];
        a[0] = 1;
        a[1] = 2;
    }
}