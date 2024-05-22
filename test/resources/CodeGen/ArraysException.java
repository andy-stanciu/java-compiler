class Main {
    public static void main(String[] args) {
        System.out.println(new Broken().illegal(new int[5]));
    }
}

class Broken {
    public int illegal(int[] arr) {
        System.out.println(1);
        System.out.println(2);
        System.out.println(3);
        if (false) {
            arr[7 - 2] = 1;
        } else {
            arr[2 - 3] = 3;
        }
        System.out.println(4);
        System.out.println(5);
        System.out.println(6);

        return 0;
    }
}