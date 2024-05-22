class Main {
    public static void main(String[] args) {
        {
            System.out.println(new A().setA(100));
            System.out.println(new B().setA(100));
            System.out.println(new B().init(1, true, new int[2]));
        }
    }
}

class A {
    int a;
    boolean b;
    int[] iA;
    public int init(int val1, boolean val2, int[] val3) {
        a = val1;
        b = val2;
        iA = val3;
        return val3[0] + 17;
    }
    public int setA(int assign) {
        a = assign + 1;
        return a * 2;
    }
    public int setIA(int[] arr) {
        arr[0] = 2002;
        arr[arr.length - 1] = 1;
        iA = arr;
        return iA[0];
    }
}

class B extends A {
    public int setA(int assign) {
        a = assign + 2;
        return a * 3;
    }
}