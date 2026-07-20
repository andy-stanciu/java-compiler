class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        Box[] boxes;

        boxes = new Box[1];
        boxes[0] = null;

        System.out.println("before null array element method call");
        System.out.println(boxes[0].getValue());
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