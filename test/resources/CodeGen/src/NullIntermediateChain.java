class Main {
    public static void main(String[] args) {
        new Test().run();
    }
}

class Test {
    public void run() {
        Link first;

        first = new Link(1);
        first.next = null;

        System.out.println("before null intermediate chain");
        System.out.println(first.next.next.value);
        System.out.println("unreachable");
    }
}

class Link {
    int value;
    Link next;

    public Link(int value) {
        this.value = value;
        this.next = null;
    }
}