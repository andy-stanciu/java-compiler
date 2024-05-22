class Main {
    public static void main(String[] args) {
        System.out.println(new Flow().test());
    }
}

class Flow {
    public int test() {
        int i;
        i = 0;
        while (i < 10) {
            System.out.println(i);
            i = i + 1;
        }
        return 0;
    }
}