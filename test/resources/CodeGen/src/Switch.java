class Main {
    public static void main(String[] args) {
        new Driver().test();
    }
}

class Driver {
    public void test() {
        int i;

        for (i = 0; i <= 10; i++) {
            switch (i) {
                case 0:
                    System.out.println(0);
                    break;
                case 1:
                    System.out.println(1);
                    break;
                case 2:
                    System.out.println(2);
                    break;
                case 3:
                case 4:
                case 5:
                    System.out.println(5);
                    break;
                case 6:
                    System.out.println(6);
                case 7:
                case 8:
                    System.out.println(8);
                case 9:
                    System.out.println(9);
                default:
                    System.out.println(10);
            }
        }

        return;
    }
}