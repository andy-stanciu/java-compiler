class Main {
    public static void main(String[] args) {

    }
}

class Test {
    public int fail1() {
        int x;
        x++;
    }

    public boolean fail2() {
        int y;
        y += 2;
    }

    public int fail3() {}

    public void success1() {}

    public void success2() {
        int x;
        x += 5;
    }

    public void success3() {
        return;
    }

    public int success4() {
        return 0;
    }

    public int success5() {
        int x;
        x = 3;
        x *= 2;
        return x;
    }

    public int fail4() {
        int x;
        x += 5;
        return x;
        x *= 2;
        return x;
    }

    public void fail5() {
        return;
        return;
    }
}