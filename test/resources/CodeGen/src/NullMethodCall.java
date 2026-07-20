class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        Box box;

        box = null;
        System.out.println("before null method call");
        System.out.println(box.getValue());
        System.out.println("unreachable");
    }
}

class Box {
    int value;

    public Box() {
        value = 123;
    }

    public int getValue() {
        return value;
    }
}