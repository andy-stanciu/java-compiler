class Precedence {
    public static void main(String[] args) {
        foo = 1;  // single statement is required apparently
    }
}

class Test {
    public int associativity() {
        a = a + b.f();
        c = d.g() * (a[4 * f.h() - 1] + 1);
        foo = f.a().b().c();
        a[5] = a[1 + g.h()];
        arr = f.g() + 1 + arr.length;
        x = new int[f.g() * a.c()];
        y = (new Bar()).foo(1 - a[2]);

        return 0;
    }

    public int math() {
        a = 1 * 2 - 2 + 3 * 4 - 5 - 6 * (7 + 1) * 23;

        return 0;
    }

    public int precedence() {
        a = 1 + 2 + 3 + 4 + 5;  // left
        b = 1 - 2 - 3 - 4 - 5;  // left
        c = w && x && y && z; // left
        d = 1 < 2 < 3 < 4 < 5;  // left
        e = 1 * 2 * 3 * 4 * 5;  // left
        x = y;  // right, but doesn't really matter since we can't chain equals in minijava grammar

        return 0;
    }

    public int bool() {
        if (!!!!!!!cond) {  // right
            a = new b();
        } else {
            c = new d();
        }

        return 0;
    }
}