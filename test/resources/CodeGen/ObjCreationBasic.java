class Main {
    public static void main(String[] args) {
        {
            System.out.println((new A()).print1());
            System.out.println((new A()).print2());
            System.out.println((new A()).print3());
        }
    }
}

class A {
    public int print1() {
        System.out.println(1);
        return 0;
    }

    public int print2() {
        System.out.println(2);
        return 0;
    }

    public int print3() {
        System.out.println(3);
        return 0;
    }
}