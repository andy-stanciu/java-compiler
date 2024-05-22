class Main {
    public static void main(String[] args) {
        System.out.println(new A().run());
    }
}

class A {
    public int run() {
        Point point;
        int ret;

        point = new Point();
        point = point.init(3, 5);
        if (point.getX() < point.getY()) {
            ret = point.getX();
        } else {
            ret = point.getY();
        }
        return ret;
    }
}

class Point {
    int x;
    int y;

    public Point init(int ax, int ay) {
        x = ax;
        y = ay;
        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}