class Main {
    public static void main(String[] args) {
        A a = new A();
    }
}

class A {
    int x = 2;
    public A() {
        System.out.println(x);
        x = 1;
        System.out.println(x);
    }
}