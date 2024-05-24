class BasicArrayTest {
    public static void main(String[] args) {
        System.out.println(new A().method1());
    }
}

class A {
    public int method1() {
        int[] arr;
        int[] arr2;
        int i;
        int len;
        arr2 = new int[20];
        arr2[8] = 3;
        len = 5;
        i = 0;
        arr = new int[len*2];

        while(i < (len*2)) {
            arr[i] = i;
            i = i+1;
        }

        return arr[arr2[2*4]+3];
    }
}