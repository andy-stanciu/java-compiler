class PlusMinusTimesExpressionTest {
    public static void main(String[] args) {
        System.out.println(new A().method1());
    }
}

class A {
    public int method1() {
        int a;
        a = 2 - 3 * 4 + 1;
        return a;
    }
}