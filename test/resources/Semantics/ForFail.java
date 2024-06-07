class Main {
    public static void main(String[] args) {
        System.out.println(1);
    }
}

class For {
    public void test() {
        int i;
        for (i = 0; i < 10; i++) {
            System.out.println(1);
        }

        for (;;) {
            System.out.println(999);
        }

        for (; false; ) {
            System.out.println(2);
        }

        for (; 1; ) {
            System.out.println(5);
        }

        for (i = 10; i >= 0; --i) {
            System.out.println(2);
        }

        for (System.out.println(3); true; System.out.println(4)) {
            ;;;
        }
        return;
    }
}