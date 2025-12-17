class Main {
    public static void main(String[] args) {
        System.out.println("Hello world");
        new Foo().m();
    }
}

class Foo {
    public void m() {
        String abc = "abc";
        String def = "def";
        System.out.println(abc);
        System.out.println(def);
        System.out.println(abc + def);
        String result = "";
        for (int i = 0; i < 3; i++) {
            result += "a";
            System.out.println(result);
        }
        for (int i = 0; i < 3; i++) {
            result += "b";
            System.out.println(result);
        }
        for (int i = 0; i < 10; i++) {
            result += abc + def;
            System.out.println(result);
        }
        System.out.println("");
        System.out.println(true);
        System.out.println(false);
        System.out.println("foo" + true);
        System.out.println("foo" + false);
        System.out.println(true + "bar");
        System.out.println(false + "bar");
        System.out.println("hello" + 123);
        System.out.println(456 + "world");
        System.out.println("magic number: " + -2373846);
        System.out.println("zero: " + 0);
        System.out.println("minus zero: " + -0);
        System.out.println("unary plus: " + +19287);
        System.out.println("woah, " + (10 + 5) + " is the same as " + (3 * 5));
        System.out.println("My top " + 3 + " fav numbers: " + -42 + ", " + 3 + ", " + 11 + ".");
        System.out.println("What about this: " + 1 + 1 + 1 + true);
    }
}