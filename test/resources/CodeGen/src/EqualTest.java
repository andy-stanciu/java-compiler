class Test {
    public static void main(String[] args) {
        System.out.println(new Equals().run());
    }
}

class Equals {
    public int run() {
        int[] arr;
        int[] arr2;
        int[] arrDiff;
        A a1;
        A a2;
        A a3;
        B b;

        if (5 == 5) {
            System.out.println(4);
        } else {
            System.out.println(23);
        }

        if (3 != 8) {
            System.out.println(9);
        } else {
            System.out.println(23);
        }

        if (false == false) {
            System.out.println(3);
        } else {
            System.out.println(23);
        }

        if (true != !true) {
            System.out.println(0);
        } else {
            System.out.println(23);
        }

        arr = new int[3];
        arr2 = arr;
        arrDiff = new int[3];

        if (arr == arr2) {
            System.out.println(7);
        } else {
            System.out.println(23);
        }

        if (arr != arrDiff) {
            System.out.println(5);
        } else {
            System.out.println(23);
        }

        a1 = new A();
        a2 = a1;
        a3 = new A();
        b = new B();

        if (a1 == a2) {
            System.out.println(9);
        } else {
            System.out.println(23);
        }

        if (a1 != a3) {
            System.out.println(23);
        } else {
            System.out.println(23);
        }
        return 0;
    }
}

class A {}
class B {}