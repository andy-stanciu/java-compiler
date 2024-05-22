class ClassVariableOffsetsTest {
    public static void main(String[] args) {
        System.out.println(5);
    }
}

class A {
    public int A1(int a, boolean b) {
        int c;
        return 0;
    }

    public int A2(int x) {
        return 0;
    }
}

class B extends A {
    public boolean B1() {
        return true;
    }

}

class C extends B {
    public int C1() {
        A a;
        int b;
        C c;
        return 0;
    }
}
