class asllow {

    public static void main(String[] args) {
        int[] andy = new int[4];
        for (int i = 0; i < andy.length; i++) {
            System.out.println(andy[i]);
        }
        haha heet = new haha();
        boolean[] hello = heet.count(andy[2] + 2);
        heet.noneSense = new boolean[3];
        for (int i = 0; i < heet.noneSense.length; i++) {
            (heet.noneSense)[i] = false;
            if (i == 1) {
                heet.noneSense[i] = true;
            } else if (i == 2) {
                heet.noneSense[i] = heet.adjecent;
            }
        }
        heet.noneSense[0] = false;
        System.out.println(heet.heheht);
    }
}

class haha {
    int heheht;
    boolean adjecent;
    boolean[] noneSense;
    int[] csMajors;


    public boolean[] count(int count) {
        System.out.println(this.heheht);
        this.heheht = count;
        System.out.println(this.heheht);
        this.heheht += count;
        this.noneSense = new boolean[count];
        this.csMajors = new int[count];

        this.noneSense[count - 2] = !this.adjecent;
        return this.noneSense;
    }
}