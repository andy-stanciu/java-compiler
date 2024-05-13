class Main {
    public static void main(String[] args) {
        {}
    }
}

class One {
    int foo;
    int bar;
    public int setFoo() { foo = 1; return 0; }
    public int getFoo() { return foo; }
    public int setBar(int bar) { return 0; }
    public int getBar() { return bar; }
}

class Two extends One {
    int bar;
    public int setFoo() {
        foo = 2; bar = 3; return 0;
    }

    public int getIt() { return bar; }
    public int resetIt() { return 42; }
}