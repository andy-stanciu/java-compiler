class Call {
    public static void main(String[] args) {
        System.out.println(new A().method1(1, 2, true));
    }
}

class A {
    public int method1(int x, int y, boolean b) {
        System.out.println(x);
        System.out.println(y);
        if(b) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
        return this.method2(x);
    }

    public int method2(int x) {
        return x*4;
    }
}