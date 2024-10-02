class Main {
    public static void main(String[] args) {
        new Uninitialized().run();
    }
}

class Uninitialized {
    public void run() {
        x = 5;
        int x = 1;
        int y = x;
        y++;
        if (x < 3) {
            y--;
        } else {
            int z = y = ++x;
        }

        z = 2;
    }
}