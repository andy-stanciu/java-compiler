class Main {
    public static void main(String[] args) {
        System.out.println(new Method().tooMany(3, new int[4], false, new Method(), 2));
    }
}

class Method {
    public int tooMany(int x, int[] y, boolean b, Method m, int z) {
        return 0;
    }
}