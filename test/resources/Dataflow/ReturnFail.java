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

    public void success6() {
        boolean b;
        int x;
        if (b) return;

        x += 1;
        if (x > 5) return;

        x -= 2;
    }

    public int success7() {
        int x;
        if (x > 0) return x;
        x++;
        if (x > 4) {
            return 2;
        } else {
            return 3;
        }
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

    public int fail6() {
        int x;
        if (x > 0) {
            return 3;
        } else {
            return 0;
        }

        return 1;
    }

    public int fail7() {
        int x;
        if (x > 0) {
            return 0;
        }
    }

    public int fail8() {
        int x;
        if (x > 0) {
            return 0;
        } else if (x <= 2) {
            return 1;
        } else if (x > 4) {
            return 2;
        } else {
            if (x > 0) return 3;
        }
    }

    public int fail9() {
        int x;
        if (true) {
            return 0;
        } else {
            return 1;
        }

        x += 1;
    }

    public void success8() {
        if (false) {
            return;
        } else if (true) {
            return;
        }
    }

    public int success9() {
        int x;
        if (x > 0) {
            return 0;
        } else if (x > 1) {
            return 1;
        } else if (x > 2) {
            return 2;
        } else if (x > 3) {
            return 3;
        } else if (x > 4) {
            return 4;
        } else {
            return 5;
        }
    }
}