class ClassVariableOffsetsTest {
    public static void main(String[] args) {
        System.out.println(5);
    }
}

class A {
    int one;
    int two;
}

class B extends A {

}

class C extends B {
    int two;
}

class D extends B {
    int three;
}