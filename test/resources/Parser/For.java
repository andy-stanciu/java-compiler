class Main {
    public static void main(String[] args) {
        System.out.println(0);
    }
}

class For {
    public int noOp1() {
        ;;;;;;;
        return 0;
    }

    public int noOp2() {
        ;
        return 0;
    }

    public int test() {
        for (i = 0; i < 10; i++) {
            System.out.println(5);
        }

        for (i += 2; i >= 10; i--) {
            System.out.println(5);
            System.out.println(2);
        }

        for (System.out.println(1); i < 10; System.out.println(2)) {
            System.out.println(5);
        }

        for (; i < 10; i++) {
            System.out.println(0);
        }

        for (; ; i++) {
            System.out.println(0);
        }

        for (;;) {
            System.out.println(0);
        }

        for (i = 0; i < 10; i++) {
            for (j = 0; j < 20; j++) {
                for (k = 0; k < 30; k++) {
                    System.out.println(1);
                    System.out.println(2);
                    System.out.println(3);
                }
                System.out.println(4);
            }
            System.out.println(5);
        }

        for (;;) System.out.println(0);
        for (;x < 5;) System.out.println(0);
        for (i /= 7;x > 3;a--) System.out.println(0);
        while (true) System.out.println(0);
        return 0;
    }
}