class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        int x;
        int y;
        if (x++ < 1) {
            System.out.println(1);
        }

        if (4 >= y = 2) {
            System.out.println(3);
        }

        x = y = x = y = x = 1;
    }
}