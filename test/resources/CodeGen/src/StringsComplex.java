class StringsComplex {
    public static void main(String[] args) {
        Foo foo = new Foo();
        System.out.println(foo.getThis() + " and " + foo.getThat());
        for (int i = 0; i < 10; i++) {
            System.out.println(foo.explode());
        }
    }
}

class Foo {
    int bar;

    public String getThat() {
        return "that";
    }

    public String getThis() {
        return "this";
    }

    public String explode() {
        bar++;
        String ret = getThis();
        for (int i = 0; i < bar; i++) {
            ret += " and a " + 1 + i % 2 == 0 + " cool";
            ret += getThat() + " and a " + 2 + i % 2 == 1 + " cool";
        }
        return ret + " done!";
    }

    public String test() {
        String x = "test";
        x += 1;
        return "bar";
    }
}

