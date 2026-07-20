class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        int[] values;

        values = null;
        System.out.println("before null int array write");
        values[0] = 123;
        System.out.println("unreachable");
    }
}