class Main {
    public static void main(String[] args) {
        (new Test()).print1();
        new Test().print2();
        new Test().print3();
    }
}

class Test {
    public void print1() {
        System.out.println(1);
        return;
    }

    public void print2() {
        System.out.println(2);
        return;
    }

    public void print3() {
        System.out.println(3);
        return;
    }
}