class Main {
    public static void main(String[] args) {
        System.out.println(new Driver().populate(new int[5]));
    }
}

class Driver {
    public int populate(int[] arr) {
        arr[2] = 3;
        return arr[2];
    }
}