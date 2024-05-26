class Test {
    public static void main(String[] args) {
        System.out.println(new Operators().run());
    }
}

class Operators {
    public int run() {
        int r;
        
        r = 4 / 2;
        System.out.println(r);
        r = 4 / (0 - 2);
        System.out.println(r);
        r = 5 / 5;
        System.out.println(r);
        r = 82 / 34;
        System.out.println(r);
        r = (0 - 48) / (0 - 54);
        System.out.println(r);
        r = (0 - 5) / 5;
        System.out.println(r);

        r = 4 % 2;
        System.out.println(r);
        r = 4 % (0 - 2);
        System.out.println(r);
        r = 5 % 5;
        System.out.println(r);
        r = 82 % 34;
        System.out.println(r);
        r = (0 - 48) % (0 - 54);
        System.out.println(r);
        r = (0 - 5) % 5;
        System.out.println(r);

        r = 4 << 2;
        System.out.println(r);
        r = 4 << (0 - 2);
        System.out.println(r);
        r = 5 << 5;
        System.out.println(r);
        r = (0 - 48) << (0 - 54);
        System.out.println(r);
        r = (0 - 5) << 5;
        System.out.println(r);

        r = 4 >> 2;
        System.out.println(r);
        r = 4 >> (0 - 2);
        System.out.println(r);
        r = 5 >> 5;
        System.out.println(r);
        r = (0 - 48) >> (0 - 54);
        System.out.println(r);
        r = (0 - 5) >> 5;
        System.out.println(r);

        r = 4 >>> 2;
        System.out.println(r);
        r = 4 >>> (0 - 2);
        System.out.println(r);
        r = 5 >>> 5;
        System.out.println(r);
        
        return 0;
    }
}