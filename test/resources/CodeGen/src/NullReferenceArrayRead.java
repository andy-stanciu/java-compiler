class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        Box[] boxes;
        Box box;

        boxes = null;
        System.out.println("before null reference array read");
        box = boxes[0];
        System.out.println(box);
        System.out.println("unreachable");
    }
}

class Box {
    int value;
}