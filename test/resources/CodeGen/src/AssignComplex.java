class Main {
    public static void main(String[] args) {
        System.out.println(new Assign().assignAll());
    }
}

class Assign {
    int x;
    int y;
    int z;
    int[] arr;
    public int assignAll() {
        boolean a;
        boolean b;

        arr = new int[10];
        x = 1;
        y = 2;
        z = 3;
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);

        x += y;
        System.out.println(x);
        System.out.println(y);

        z -= x;
        System.out.println(z);
        System.out.println(x);

        y *= 4;
        System.out.println(y);

        z += 2;

        arr[5] += 10;
        arr[5] /= 2;
        System.out.println(arr[5]);
        arr[5] *= 3;
        arr[5] /= z;
        System.out.println(arr[5]);

        arr[4] = 0 - 5;
        arr[4] /= 0 - 1;
        System.out.println(arr[4]);
        arr[3] = 0 - 11;
        arr[3] /= 2;
        System.out.println(arr[3]);

        x = 5;
        y = 2;
        x %= y;
        System.out.println(x);
        System.out.println(y);
        arr[2] = 0 - 6;
        arr[2] %= 4;
        System.out.println(arr[2]);
        arr[1] = 0 - 23;
        arr[1] %= 0 - 5;
        arr[1] %= 4;
        System.out.println(arr[1]);
        x = 3;
        x %= 3;
        System.out.println(x);

        a = false;
        b = true;
        x = 5;
        y = 23;
        z = 574;
        x &= y;
        System.out.println(x);
        System.out.println(y);
        z ^= x;
        System.out.println(z);
        System.out.println(x);
        y |= z;
        System.out.println(y);
        System.out.println(z);

        a |= b;
        b &= a;
        b ^= false;

        x = 2;
        y = 54234;
        z = 23662;
        x <<= 4;
        System.out.println(x);
        y >>= 2;
        System.out.println(y);
        z >>>= 3;
        System.out.println(z);
        x <<= 0 - 5;
        System.out.println(x);
        y >>= 0 - 34;
        System.out.println(y);
        z >>>= 0 - 6;
        System.out.println(z);

        x = 5;
        y = 6;

        x++;
        System.out.println(x);
        x--;
        System.out.println(x);
        ++y;
        System.out.println(y);
        --y;
        System.out.println(y);

        return 0;
    }
}