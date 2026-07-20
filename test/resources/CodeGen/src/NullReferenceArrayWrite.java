class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        Box[] boxes;

        boxes = null;
        System.out.println("before null reference array write");
        boxes[0] = new Box();
        System.out.println("unreachable");
    }
}

class Box {
    int value;

    public Box() {
        value = 123;
    }
}