class Main {
    public static void main(String[] args) {
        System.out.println(new Driver().run());
    }
}

class Driver {
    public int run() {
        int i;
        int j;
        int k;

        for (i = 0; i < 10; i++) {
            System.out.println(i + 1);
        }

        for (i = 10; i > 0; i--) {
            System.out.println(i);
        }

        for (i = 0; i < 5; i++) {
            for (j = 0; j < 5; j++) {
                for (k = 0; k < 5; k++) {
                    System.out.println(i * j * k);
                }
            }
        }

        for (; i < 0; ) {
            System.out.println(999);
        }

        for (i = 0; i < 10; i += 2) {
            System.out.println(i * 2);
        }

        for (j = 1; j <= 20; j *= 2) {
            System.out.println(j * 3);
        }

        for (i = 0; i < 10;) {
            System.out.println(i - 5);
            i++;
        }

        i = 0;
        for (; i < 10;) {
            System.out.println(i * 7);
            i += 3;
        }
        return 0;
    }
}