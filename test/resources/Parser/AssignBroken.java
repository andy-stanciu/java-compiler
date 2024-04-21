class Foo {
    public static void main(String[] args) {
        hi = 1;
    }
}
class Foo2 {
    public int illegal() {
        a = b = c;  // illegal since assignment is not an expression, only a statement
    }
}