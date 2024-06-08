class Main {
    public static void main(String[] args) {
        System.out.println(new Driver().run());
    }
}

class Driver {
    public int run() {
        int i;
        for (i = 0; i < 5; i++) {
            System.out.println(i);
        }
        return 0;
    }
}