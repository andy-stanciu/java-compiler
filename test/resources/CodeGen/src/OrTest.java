class AssignTest {
    public static void main(String[] args) {
        System.out.println(new A().m1(5));
    }
}

class A {
    public int m1(int n) {
        if(true || true) {
            System.out.println(0);
        } else {
            System.out.println(1);
        }

        if(true || false) {
            System.out.println(0);
        } else {
            System.out.println(1);
        }

        if(false || true) {
            System.out.println(0);
        } else {
            System.out.println(1);
        }

        if(false || false) {
            System.out.println(0);
        } else {
            System.out.println(1);
        }

        if((n < 10) || !false) {
            System.out.println(0);
        } else {
            System.out.println(1);
        }

        return 0;
    }
}