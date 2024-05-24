class Call {
    public static void main(String[] args) {
        System.out.println(new A().method1(true));
    }
}

class A {
    public int method1(boolean b) {
        if(b) {
            System.out.println(2);
        } else {
            System.out.println(3);
        }
        return 0;
    }
}