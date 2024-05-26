class Main {
    public static void main(String[] args) {
        System.out.println(new Exception().except(new int[4]));
    }
}

class Exception {
    public int except(int[] arr) {
        return arr[arr.length];
    }
}