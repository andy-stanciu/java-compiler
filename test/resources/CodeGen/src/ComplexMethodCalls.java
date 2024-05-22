class Call {
    public static void main(String[] args) {
        System.out.println(new A().method1(1));
    }
}

class A {
    public int method1(int x) {
        B bug;
        bug = new B();
        return bug.eatWorms(this.method2(x)).method3();
    }

    public int method2(int x) {
        System.out.println(x);
        return x*2;
    }

    public int method3() {
        System.out.println(3);
        return 4;
    }
}

class B {
    public A eatWorms(int x) {
        System.out.println(x);
        return new A();
    }
}