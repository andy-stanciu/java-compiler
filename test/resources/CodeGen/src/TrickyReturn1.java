class Main {
    public static void main(String[] args) {
        new Driver().run();
    }
}

class Driver {
    public void run() {
        int i;
        for (i = 0; i < 100; i++) {
            System.out.println(get(i));
        }
    }

    public int get(int i) {
        if (i % 2 == 0) {
            return 0;
        } else if (i % 3 == 0) {
            return 1;
        } else if (i % 4 == 0) {
            return 2;
        } else {
            return 3;
        }
    }
}