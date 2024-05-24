class Main {
    public static void main(String[] args) {
        System.out.println(new VectorDriver().run());  // 0
    }
}

class Vector3D {
    int x;
    int y;
    int z;

    public int create(int ax, int ay, int az) {
        x = ax;
        y = ay;
        z = az;
        return x + y + z;
    }

    public int copy(Vector3D other) {
        x = other.getX();
        y = other.getY();
        z = other.getZ();

        return x + y + z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}

class VectorDriver {
    public int run() {
        Vector3D v1;
        Vector3D v2;
        Vector3D v3;

        v1 = new Vector3D();
        v2 = new Vector3D();
        v3 = new Vector3D();

        System.out.println(v1.getX());  // 0
        System.out.println(v2.getY());  // 0
        System.out.println(v3.getZ());  // 0

        System.out.println(v1.create(1, 2, 3));  // 6
        System.out.println(v2.create(4, 5, 6));  // 15
        System.out.println(v3.create(7, 8, 9));  // 24

        System.out.println(v1.copy(v3));  // 24
        System.out.println(v2.copy(v3));  // 24
        return 0;
    }
}