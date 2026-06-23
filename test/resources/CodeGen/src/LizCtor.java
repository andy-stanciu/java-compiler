class Main {
    public static void main(String[] args) {
        Bug samie = new Bug(false);
        Liz liz = new Liz(new int[20]);
        liz.bite(2);
        liz.bite(15);
        liz.lick(2);
        liz.lick(9);
        liz.lick(20);
        liz.lick(40);
    }
}

class Bug {
    int[] bugs = new int[10];
    public Bug(int[] bugs) {
        this.bugs = bugs;
    }
    public Bug(boolean bug) {
        if (!bug) {
            System.out.println("SCREAM!!!");
        }
    }
}

class Liz extends Bug {
    public Liz(int[] bugs) {
        super(false);
        this.bugs = bugs;
        System.out.println("AAAAA");
    }

    public boolean bite(int attack) {
        if (attack > 5) {
            System.out.println("Bite Emma");
            return true;
        }
        return false;
    }

    public int lick(int attack) {
        if (attack == 2) {
            System.out.println("Lick Andy");
            return 1;
        }
        if (attack == 20) {
            System.out.println("Poke out Andy's tongue");
            return 0;
        }
        if (attack < 10) {
            System.out.println("Lick Andy's spoon");
            return 1;
        }
        System.out.println("Haha you failed");
        return 0;
    }
}