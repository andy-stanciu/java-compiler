class AssignTest {
    public static void main(String[] args) {
        if(new A().m1(2, false)) {
            System.out.println(1);
        } else {
            System.out.println(0);
        }
    }
}

class A {
    public boolean m1(int n, boolean b) {
        int x;
        System.out.println(n);
        x = n;
        System.out.println(x);
        n = 5;
        System.out.println(n);
        b = true;
        return b;
    }
}