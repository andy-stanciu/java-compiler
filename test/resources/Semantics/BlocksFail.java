class Main {
    public static void main(String[] args) {
        new Blocks().run();
    }
}

class Blocks {
    public void run() {
        int foo = 1;

        for (int i = 0; i < 5; i++) {
            int bar = 5;
            for (int j = 0; j < 3; j++) {
                System.out.println(i + j);

                System.out.println(foo + bar);
            }
        }

        bar++;
        if (false) {
            int x = 1;
        } else {
            x--;
        }

        while (true) {
            System.out.println(foo);
            System.out.println(j);
        }

        for (int i = 0; i < 3; i++) System.out.println(i);

        for (int i = 0; i < 5; i++) {
            System.out.println(j);

            j--;
            if (false) {
                y--;
            } else {
                int y = 1;
            }

            {
                int x = 1;
                System.out.println(x);
            }

            System.out.println(x);
        }
    }
}