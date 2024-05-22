class ArraysAsParams {
    public static void main(String[] args) {
        System.out.println(new A().method1());
    }
}
class A {
    public int method1() {
        int[] arr;
        boolean b;
        int i;
        i = 0;

        arr = new int[5];
        b = this.method2(arr);

        while(i < arr.length) {
            System.out.println(arr[i]);
            i = i + 1;
        }

        return 0;
    }

    public boolean method2(int[] arr) {
        int i;
        i = 0;
        while(i < arr.length) {
            arr[i] = i;
            i = i + 1;
        }
        return true;
    }
}