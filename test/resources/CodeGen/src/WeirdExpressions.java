class Main {
    public static void main(String[] args) {
        new Weird().run();
    }
}

class Weird {
    public void run() {
        int x = 4;
        int y = 3;
        int z = x = --y;
        System.out.println(x = 2);
        System.out.println(y = z = 1);
        System.out.println(z--);
    }
}