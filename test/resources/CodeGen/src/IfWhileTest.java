class IfWhileTest {
    public static void main(String[] args) {
        System.out.println(new A().method1());
    }
}

class A {
    // small change
    public int method1() {
        int x;
        x = 0;
        if(x < 5) {
            System.out.println(1);
        } else {
            System.out.println(2);
        }
        while(x < 10) {
            System.out.println(x);
            x = x+1;
        }
        return x;
    }
}