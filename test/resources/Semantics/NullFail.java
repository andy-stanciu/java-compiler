class Main {
    public static void main(String[] args) {
        System.out.println(new BadNull().run());
    }
}

class BadNull {
    BadNull b;
    public int run() {
        int x;
        boolean flag;
        int[] values;
        BadNull obj;

        x = null;                 // ERROR: null assigned to primitive int
        flag = null;              // ERROR: null assigned to primitive boolean

        if (null) {               // ERROR: condition must be boolean
            x = 1;
        } else {
            x = 2;
        }

        while (null) {            // ERROR: condition must be boolean
            x = x + 1;
        }

        values = null;
        x = values[null];         // ERROR: array index must be int

        null = obj;               // ERROR: null is not an assignable l-value
        null.foo();               // ERROR: cannot invoke a method on null literal

        x = null + 1;             // ERROR: null is not numeric
        String s = null + 0;
        flag = (null < obj);      // ERROR: null cannot be used with <

        return null;              // ERROR: method returns int, not reference
    }

    public void foo() {
        BadNull obj = new BadNull();
        obj.b = null;
        if (obj.b == null) {
            obj.b = null = null = null;
        }
    }
}