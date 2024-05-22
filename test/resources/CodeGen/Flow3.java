class Main {
    public static void main(String[] args) {
        System.out.println(new Flow().test());
    }
}

class Flow {
    public int test() {
        int i;
        i = 0;
        if (this.sideEffect1() && this.sideEffect2()) {
            System.out.println(7);
        } else {
            System.out.println(6);
        }
        if (this.sideEffect2() && this.sideEffect1()) {
            System.out.println(4);
        } else {
            System.out.println(5);
        }
        if (3 < 2 && this.sideEffect1()) {
            System.out.println(9);
        } else if (!(4 < 2)) {
            System.out.println(3);
        } else {
            System.out.println(1);
        }

        while (i < 5 && !this.sideEffect1()) {
            System.out.println(333);
            i = i + 1;
        }
        return 0;
    }

    public boolean sideEffect1() {
        System.out.println(111);
        return false;
    }

    public boolean sideEffect2() {
        System.out.println(222);
        return true;
    }
}