class IfWhileTest {
    public static void main(String[] args) {
        System.out.println(new A().method1(1));
    }
}

class A {
    int y;
    // small change
    public int method1(int z) {
        int x;
        y = z; // 1
        x = 0;
        if(x < 5) {
            System.out.println(1);
            if(y < 3) {
                System.out.println(2);
                while(y < 5) {
                    System.out.println(y);
                    y = y + 1;
                }
            } else {
                System.out.println(3);
            }
            System.out.println(7);
        } else {
            System.out.println(4);
            if(true) {
                System.out.println(5);
            } else {
                System.out.println(6);
            }
        }
        while(x < 10) {
            System.out.println(x);
            if(!false) {
                System.out.println(100);
            } else {
                {}
            }
            x = x+3;
        }
        return 0;
    }
}