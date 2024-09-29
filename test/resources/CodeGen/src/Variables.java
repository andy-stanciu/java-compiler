class Main {
    public static void main(String[] args) {
        int x = 1;
        System.out.println(x);
        int y = x + 1;
        System.out.println(y);
        int z = y + x;
        System.out.println(x);

        for (int i = 0; i < 10; i++) {
            int w = i * x + i * y + i * z;
            System.out.println(w);
        }
    }
}