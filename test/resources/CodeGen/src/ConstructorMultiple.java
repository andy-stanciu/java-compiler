class Main {
    public static void main(String[] args) {
        Bar b = new Bar();
        new Printer(b).print();
        b = new Bar(1, 2);
        new Printer(b).print();
        b = new Bar(7, 8, 9);
        new Printer(b).print();
        b = new Bar(5);
        new Printer(b).print();
    }
}

class Printer {
    Bar b;
    public Printer(Bar b) {
        this.b = b;
    }
    public void print() {
        System.out.println(b.getX());
        System.out.println(b.getY());
        System.out.println(b.getZ());
    }
}

class Bar {
    int x;
    int y;
    int z;

    public Bar(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getY() {
        return y;
    }

    public Bar(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public Bar(int x) {
        this.x = x;
    }
    public Bar() {
        this.x = this.y = this.z = 3;
    }

    public int getX() {
        return x;
    }
}