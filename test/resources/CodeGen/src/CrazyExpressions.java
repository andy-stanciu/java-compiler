class Main {
    public static void main(String[] args) {
        new Crazy().test();
    }
}

class Crazy {
    public void test() {
        int x;
        boolean b;
        x = 0;
        b = false;

        System.out.println(x = 2);
        if ((x = 3) < 4) {
            System.out.println(9);
        }

        int[] foo = new int[5];
        foo[x = 2] = 1;

        call(x = x = x = 2, x = 3);
        if (x++ < 3) {
            System.out.println(4);
        }

        x = (b = true) ? ++x : --x;
        if (((foo = new int[3])[2]) == 0) {
            System.out.println(1);
        }

        if (( + ++x) + 1 < 3) {
            System.out.println(7);
        }

        if (!(b = false)) {
            System.out.println(23);
        }

        x = ~x++;
    }

    public void call(int x, int y) {

    }
}