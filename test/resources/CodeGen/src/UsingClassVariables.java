class UsingClassVariables {
    public static void main(String[] args) {
        System.out.println(new A().method1());
    }
}

class A {
    A a;
    int i;
    public int method1() {
        i = 5;
        if(i < 10) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        a = new A();
        i = a.method2() + 2;
        return i;
    }

    public int method2() {
        return 3;
    }

}