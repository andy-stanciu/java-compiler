class Main {
    public static void main(String[] args) {
        {}
    }
}

class New {
    int x;
    int y;
    int z;

    public int test() {
        boolean a;
        boolean b;
        Foo foo;

        if (a || b) {
            System.out.println(1);
        } else if (a == b) {
            System.out.println(2);
        } else if (a != b) {
            System.out.println(3);
        } else {
            System.out.println(4);
        }

        x = y >= z ? y : z;
        while (y > x) {
            y = y + 1;
            if (x <= y || y < z) {
                System.out.println(5);
            } else {
                System.out.println(6);
            }
        }

        x = x | 4;
        x = y & 2;
        y = z ^ x;
        z = (x + 1) / (4 * y);
        y = y % z;
        z = ~y;

        x = 1;
        y += 2;
        z -= y;
        x *= 4;
        z /= 2;
        x %= 2;
        y &= 5;
        z |= (y & 1);
        y ^= (x ^ x);
        z <<= 1;
        y >>= z;
        y >>>= 4;

        x = (x > y) ? x << 1 : y >> 2;
        z = (z == y) ? z >>> 3 : y | 1;
        x++;
        y--;
        ++z;
        --y;

        foo = new Bar();
        if (foo instanceof Bar) {
            z /= foo.getA() >> 2;
        } else {
            z--;
        }

        return 0;
    }

    public int min(int a, int b) {
        return a < b ? a : b;
    }

    public int max(int a, int b) {
        return a > b ? a : b;
    }

    public int crazyTernary(int a, int b, int c, int d) {
        return a > b ? (b >= c ? (c <= d ? d : c) : b) : a;
    }
}

class Foo {
    int a;

    public int getA() { return a; }
}

class Bar extends Foo {
    public int getBaz() { return 0; }
}