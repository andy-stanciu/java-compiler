class ObjectsAsParams {
    public static void main(String[] args) {
        System.out.println(new A().method1(new A()));
    }
}

class A {
    int a;
    public int method1(A other) {
        int r;
        r = this.setA(2);
        r = other.setA(3);
        if (this.getA() < other.getA()) {
            System.out.println(0); // should print 0
        } else {
            System.out.println(1);
        }
        return 5;
    }

    public int setA(int x) {
        a = x;
        return 0;
    }

    public int getA() {
        return a;
    }
}