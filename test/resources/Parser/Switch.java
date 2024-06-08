class Main {
    public static void main(String[] args) {

    }
}

class Switch {
    public void test() {
        int x;
        switch (x) {
            case 0:
                a();
                b();
                break;
            case 1:
                a();
                break;
            case 2:
                b();
                break;
            case 3:
                break;
            case 4:
            case 5:
                b();
            case 6:
            case 7:
                a();
                break;
            case 8:
                a();
                b();
        }

        return;
    }

    public void a() { return; }
    public void b() { return; }
}