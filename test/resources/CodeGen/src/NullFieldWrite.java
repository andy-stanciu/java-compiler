class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        Box box;

        box = null;
        System.out.println("before null field write");
        box.value = 123;
        System.out.println("unreachable");
    }
}

class Box {
    int value;
}