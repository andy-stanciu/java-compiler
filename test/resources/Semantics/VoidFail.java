class Main {
    public static void main(String[] args) {
        System.out.println(1);
    }
}

class Void {
    public void a() {
        int x;
        x++;
        return;
    }

    public void b() {
        return this.a();
    }

    public void x() {
        int a;
        int b;
        a = this.a();
        b = this.b();
        return this.x();
    }
}