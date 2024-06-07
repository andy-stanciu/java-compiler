class Main {
    public static void main(String[] args) {
        System.out.println(2);
    }
}

class Void {
    public void test1() {
        return;
    }

    public void test2() {
        x = this.test1();
        return;
    }
}