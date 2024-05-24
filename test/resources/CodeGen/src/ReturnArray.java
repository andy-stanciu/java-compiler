class ReturnArray {
    public static void main(String[] args) {
        System.out.println(new A().method1());
    }
}

class A {
    public int method1() {
        int[] arr;
        int i;
        arr = this.method2();
        i = 0;
        while(i < arr.length) {
            System.out.println(arr[i]);
            i = i + 1;
        }
        return 0;
    }

    public int[] method2() {
        int[] arr;
        arr = new int[10];
        arr[9] = 9;
        return arr;
    }

}