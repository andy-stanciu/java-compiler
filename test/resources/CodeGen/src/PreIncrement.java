class Main {
    public static void main(String[] args) {
        new Increment().run();
    }
}

class Increment {
    public void run() {
        int b = 1;
        System.out.println(b++);
        System.out.println(b);

        int x = 4;
        System.out.println(++x);
        System.out.println(x++);
        System.out.println(x--);
        System.out.println(--x);

        int y = 2;
        System.out.println(x++ - y--);
        System.out.println(x);

        int z = ++x - ++y;
        System.out.println(z++);
        System.out.println(--z);

        int a = x = y = --z;
        System.out.println(a);
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);
    }
}