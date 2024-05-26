class Test {
    public static void main(String[] args) {
        System.out.println(new Ternary().run());
    }
}

class Ternary {
    boolean b;
    boolean b2;
    public int run() {
        int x;
        int[] arr;
        int i;

        b = false;
        this.b2 = true;

        x = this.b ? 6 : 9;
        System.out.println(x);
        x = this.b2 ? 23 : 0;
        System.out.println(x);

        if (this.b ? false : this.b2) {
            System.out.println(46);
        } else {
            System.out.println(656);
        }

        arr = new int[50];
        i = 0;
        while (i < arr.length) {
            arr[i] = i;
            System.out.println(arr[i]);
            i++;
        }

        i = 0;
        while (i < arr.length) {
            arr[i] = (arr[i] % 2 == 0) ? arr[i] / 2 : arr[i] ^ arr[i];
            System.out.println(arr[i]);
            i++;
        }

        return 0;
    }
}