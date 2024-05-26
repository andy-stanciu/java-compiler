class Main {
    public static void main(String[] args) {
        System.out.println(new Exception().except(4));
    }
}

class Exception {
    public int except(int x) {
        x %= 0;
        return 0;
    }
}