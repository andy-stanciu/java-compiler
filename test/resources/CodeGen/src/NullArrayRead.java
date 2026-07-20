class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        int[] values;

        values = null;
        System.out.println("before null int array read");
        System.out.println(values[0]);
        System.out.println("unreachable");
    }
}