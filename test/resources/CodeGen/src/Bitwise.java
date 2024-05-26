class Test {
    public static void main(String[] args) {
        System.out.println(new Bitwise().run());
    }
}

class Bitwise {
    public int run() {
        int r;
        int w;
        int x;
        int y;
        int z;
        boolean a;
        boolean b;
        boolean c;

        w = 0 - 23;
        x = 0 - 12;
        y = 347;
        z = 926;

        r = x & y;
        System.out.println(r);
        r = x & w;
        System.out.println(r);
        r = z & w;
        System.out.println(r);
        r = x | y;
        System.out.println(r);
        r = x | w;
        System.out.println(r);
        r = z | w;
        System.out.println(r);
        r = x ^ y;
        System.out.println(r);
        r = x ^ w;
        System.out.println(r);
        r = z ^ w;
        System.out.println(r);
        r = ~z ^ ~x;
        System.out.println(r);
        r = ~y;
        System.out.println(r);

        a = true;
        b = false;

        c = a & b;
        System.out.println(r);
        c = a | b;
        System.out.println(r);
        c = a & (!b);
        System.out.println(r);
        c = a ^ b;
        System.out.println(r);

        if (a | b) {
            System.out.println(34);
        } else if (a ^ b) {
            System.out.println(23);
        } else {
            System.out.println(0);
        }

        if (b & a) {
            System.out.println(8);
        } else {
            System.out.println(9);
        }
        return 0;
    }
}