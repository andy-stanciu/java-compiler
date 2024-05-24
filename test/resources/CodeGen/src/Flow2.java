class Main {
    public static void main(String[] args) {
        System.out.println(new Flow().test());
    }
}

class Flow {
    public int test() {
        if (4 < 4) {
            System.out.println(0);
        } else if (6 < 5) {
            System.out.println(1);
        } else if (0 < 1) {
            System.out.println(2);
        } else {
            System.out.println(3);
        }
        return 0;
    }
}