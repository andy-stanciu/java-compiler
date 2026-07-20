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

        System.out.println("before null array element field read");
        System.out.println(boxes[0].value);
        System.out.println("unreachable");
    }
}

class Box {
    int value;
}