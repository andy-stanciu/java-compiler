class Main {
    public static void main(String[] args) {
        {
            System.out.println(new Driver().run().extra());
        }
    }
}

class Driver {
    One one;
    boolean b;

    public int extra() {
        int r;
        b = false;
        if (this.b) {
            System.out.println(23);
        } else {
            System.out.println(3);
        }

        one = new One();
        r = this.one.setFoo();
        r = this.one.setBar(348);
        System.out.println(this.one.foo);
        System.out.println(this.one.bar);
        return 0;
    }

    public Driver run() {
        Two two;
        One one;
        int r;

        two = new Two();
        one = two;

        r = one.setFoo();
        System.out.println(one.getFoo());

        r = one.setBar(17);
        r = two.setFoo();
        System.out.println(two.getBar());
        System.out.println(two.getIt());

        r = two.resetIt();
        System.out.println(two.getBar());
        System.out.println(two.getIt());
        return this;
    }
}

class One {
    int foo;
    int bar;
    public int setFoo() { this.foo = 1; return 0; }
    public int getFoo() { return this.foo; }
    public int setBar(int bar) { this.bar = bar; return 0; }
    public int getBar() { return this.bar; }
}

class Two extends One {
    int bar;

    public int setFoo() {
        this.foo = 2; this.bar = 3; return 0;
    }

    public int getIt() {
        return this.bar;
    }

    public int resetIt() { return 0; }
}