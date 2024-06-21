class Main {
    public static void main(String[] args) {

    }
}

class Test {
    public int fail1() {
        while (false) {
            return 0;
        }
    }

    public int fail2() {
        int x;
        while (true) {
            x++;
            x--;
            x *= 4;
            return x;
        }
    }

    public int fail3() {
        int i;
        for (i = 0; i < 10; i++) {
            i *= 3;
            if (i > 4) {
                return i;
            }
        }
    }

    public void fail4() {
        int x;
        return;
        x++;
        return;
        x--;
        return;
        x *= 2;
    }
}