class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        int[] values;

        values = null;
        System.out.println("before null array length");
        System.out.println(values.length);
        System.out.println("unreachable");
    }
}