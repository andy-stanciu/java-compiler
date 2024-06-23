class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        int x;
        int y;

        x = -1;
        System.out.println(x);
        y = -3;
        System.out.println(y);
        System.out.println(x * y);
        x = +5;
        System.out.println(x * y);
        System.out.println(-x * -y);
        System.out.println(-y);
        System.out.println(-x);
        System.out.println(-y);
        System.out.println(-x - -y);
        System.out.println(-x - -y + x - -y);
    }
}