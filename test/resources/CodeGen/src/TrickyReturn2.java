class Main {
    public static void main(String[] args) {
        new Driver().run();
    }
}

class Driver {
    public int run() {
        int i;
        for (i = 0; i < 10; i++) {
            System.out.println(i);
            if (i > 5) {
                return 0;
            }
        }
        return 0 - 1;
    }
}