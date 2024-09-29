class MultiArrays {
    public static void main(String[] args) {
        new Driver().run();
    }
}

class Foo {
    int a;
    int b;
}

class Driver {
    public void run() {
        System.out.println(222);
        Foo[] arr1 = new Foo[10];
        System.out.println(111);
        for (int i = 0; i < arr1.length; i++) {
            Foo f = new Foo();
            f.a = i;
            f.b = i * 2;
            arr1[i] = f;
        }

        for (int j = 0; j < arr1.length; j++) {
            System.out.println(arr1[j].a);
            System.out.println(arr1[j].b);
        }

        Foo[][][][][] arr2 = new Foo[7][5][3][20][6];
        System.out.println(arr2.length);
        System.out.println(arr2[0].length);
        System.out.println(arr2[0][0].length);
        for (int k = 0; k < arr2.length; k++) {
            for (int l = 0; l < arr2[k].length; l++) {
                for (int m = 0; m < arr2[k][l].length; m++) {
                    for (int n = 0; n < arr2[k][l][m].length; n++) {
                        for (int o = 0; o < arr2[k][l][m][n].length; o++) {
                            Foo f2 = new Foo();
                            f2.a = k + l + m + n + o;
                            f2.b = (k + l + m + n + o) * 2;
                            arr2[k][l][m][n][o] = f2;
                        }
                    }
                }
            }
        }

        for (int d = 0; d < arr2.length; d++) {
            for (int e = 0; e < arr2[d].length; e++) {
                for (int g = 0; g < arr2[d][e].length; g++) {
                    for (int h = 0; h < arr2[d][e][g].length; h++) {
                        for (int z = 0; z < arr2[d][e][g][h].length; z++) {
                            System.out.println(arr2[d][e][g][h][z].b);
                        }
                    }
                }
            }
        }
    }
}