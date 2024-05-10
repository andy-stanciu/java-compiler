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