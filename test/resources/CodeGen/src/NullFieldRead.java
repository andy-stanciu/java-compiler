class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        Box box;

        box = null;
        System.out.println("before null field read");
        System.out.println(box.value);
        System.out.println("unreachable");
    }
}

class Box {
    int value;

    public Box() {
        value = 123;
    }
}