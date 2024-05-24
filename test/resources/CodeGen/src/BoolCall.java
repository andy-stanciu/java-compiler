class Main {
    public static void main(String[] args) {
        System.out.println(new Test().run());
    }
}

class Test {
    boolean b;
    public int run() {
        boolean g;
        g = this.init();
        if (this.check()) {
            System.out.println(0);
        } else {
            System.out.println(1);
        }
        return 1000000;
    }

    public boolean init() {
        b = false;
        return true;
    }

    public boolean check() {
        return b;
    }
}