class Main {
    public static void main(String[] args) {
        System.out.println(new Driver().run());
    }
}

class Driver {
    int[] arr;
    int[] arr2;
    public int run() {
        int i;
        arr = new int[10];
        arr2 = new int[5];
        i = 0;
        while (i < arr.length) {
            arr[i] = i + 1;
            i = i + 1;
        }
        i = 0;
        while (i < arr2.length) {
            arr2[i] = arr2.length - i;
            i = i + 1;
        }

        return this.printArrays();
    }

    public int printArrays() {
        int i;
        int x;
        i = 0;
        while (i < arr.length) {
            x = arr[i];
            System.out.println(x);
            i = i + 1;
        }
        i = 0;
        while (i < arr2.length) {
            x = arr2[i];
            System.out.println(x);
            i = i + 1;
        }
        return 0;
    }
}