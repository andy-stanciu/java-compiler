class ArraysAsParams {
    public static void main(String[] args) {
        System.out.println(new A().method1());
    }
}
class A {
    public int method1() {
        if(5 < 6) {
            System.out.println(0);
        } else {
            System.out.println(1);
        }

        if(5 < 5) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        if(5 < 4) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }

        return 5;
    }
}