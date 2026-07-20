class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        Holder holder;

        holder = new Holder();
        holder.box = null;

        System.out.println("before null object field method call");
        System.out.println(holder.box.getValue());
        System.out.println("unreachable");
    }
}

class Holder {
    Box box;

    public Holder() {
        box = new Box();
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