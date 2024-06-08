class Main {
    public static void main(String[] args) {
        System.out.println(new Driver().run());
    }
}

class Driver {
    public int run() {
        int x;
        x = a();
        x = b();
        return c();
    }

    public int a() {
        System.out.println(1);
        return 0;
    }

    public int b() {
        System.out.println(2);
        return 0;
    }

    public int c() {
        return 3;
    }
}