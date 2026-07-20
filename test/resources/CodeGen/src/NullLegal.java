class Main {
    public static void main(String[] args) {
        Driver d = new Driver();
        d.run();
    }
}

class Driver {
    public void run() {
        Node head;
        Node tail;
        Node cursor;
        Node found;
        Node[] nodes;
        Node[] emptyNodes;
        int[] numbers;
        int[] noNumbers;
        String text;
        String message;

        System.out.println("== direct null values ==");

        head = null;
        tail = null;
        nodes = null;
        numbers = null;
        text = null;

        System.out.println(head);
        System.out.println(tail);
        System.out.println(nodes);
        System.out.println(numbers);
        System.out.println(text);

        System.out.println("== equality ==");

        System.out.println(head == null);
        System.out.println(null == head);
        System.out.println(head != null);
        System.out.println(head == tail);
        System.out.println(nodes == null);
        System.out.println(numbers != null);
        System.out.println(text == null);
        System.out.println(null == null);

        System.out.println("== instanceof ==");

        System.out.println(head instanceof Node);
        System.out.println(head instanceof SpecialNode);

        System.out.println("== string operations ==");

        message = "start:";
        message = message + null;
        System.out.println(message);

        message += ":middle:";
        message += null;
        System.out.println(message);

        text = null;
        message = "text=" + text;
        System.out.println(message);

        System.out.println("== arrays containing null ==");

        nodes = new Node[6];
        numbers = new int[6];

        System.out.println(nodes[0] == null);
        System.out.println(nodes[5] == null);
        System.out.println(numbers[0]);

        nodes[0] = new Node(10);
        nodes[1] = null;
        nodes[2] = new SpecialNode(20, 200);
        nodes[3] = null;
        nodes[4] = new Node(30);
        nodes[5] = null;

        numbers[0] = 3;
        numbers[1] = 1;
        numbers[2] = 4;
        numbers[3] = 1;
        numbers[4] = 5;
        numbers[5] = 9;

        System.out.println(nodes[0] == null);
        System.out.println(nodes[1] == null);
        System.out.println(nodes[2] instanceof Node);
        System.out.println(nodes[2] instanceof SpecialNode);
        System.out.println(nodes[3] instanceof SpecialNode);
        System.out.println(nodes[4].value);
        System.out.println(numbers[0] + numbers[5]);

        System.out.println("== nullable linked list ==");

        head = new Node(1);
        head.next = new Node(2);
        head.next.next = null;

        tail = new Node(99);
        tail.next = null;

        System.out.println(length1(head));
        System.out.println(length1(tail));
        System.out.println(length1(null));

        System.out.println(sum(head));
        System.out.println(sum(tail));
        System.out.println(sum(null));

        System.out.println(contains(head, 1));
        System.out.println(contains(head, 2));
        System.out.println(contains(head, 3));
        System.out.println(contains(null, 0));

        found = find(head, 2);
        System.out.println(found == null);
        System.out.println(found.value);

        found = find(head, 3);
        System.out.println(found == null);

        found = find(null, 1);
        System.out.println(found == null);

        System.out.println("== nullable array algorithm ==");

        System.out.println(countNonNull(nodes));
        System.out.println(sumNonNull(nodes));
        System.out.println(countSpecial(nodes));
        System.out.println(firstNonNull(nodes).value);

        emptyNodes = new Node[4];
        System.out.println(countNonNull(emptyNodes));
        System.out.println(sumNonNull(emptyNodes));
        System.out.println(firstNonNull(emptyNodes) == null);

        System.out.println("== null assignment after object creation ==");

        cursor = new Node(7);
        System.out.println(cursor.value);
        cursor = null;
        System.out.println(cursor == null);

        cursor = new SpecialNode(8, 80);
        System.out.println(cursor instanceof Node);
        System.out.println(cursor instanceof SpecialNode);
        cursor = null;
        System.out.println(cursor instanceof SpecialNode);

        System.out.println("== done ==");
    }

    public int length1(Node node) {
        int result;

        result = 0;
        while (node != null) {
            result = result + 1;
            node = node.next;
        }

        return result;
    }

    public int sum(Node node) {
        int result;

        result = 0;
        while (node != null) {
            result = result + node.value;
            node = node.next;
        }

        return result;
    }

    public boolean contains(Node node, int target) {
        while (node != null) {
            if (node.value == target) {
                return true;
            }
            node = node.next;
        }

        return false;
    }

    public Node find(Node node, int target) {
        while (node != null) {
            if (node.value == target) {
                return node;
            }
            node = node.next;
        }

        return null;
    }

    public int countNonNull(Node[] nodes) {
        int i;
        int result;

        i = 0;
        result = 0;

        while (i < nodes.length) {
            if (nodes[i] != null) {
                result = result + 1;
            }
            i = i + 1;
        }

        return result;
    }

    public int sumNonNull(Node[] nodes) {
        int i;
        int result;

        i = 0;
        result = 0;

        while (i < nodes.length) {
            if (nodes[i] != null) {
                result = result + nodes[i].value;
            }
            i = i + 1;
        }

        return result;
    }

    public int countSpecial(Node[] nodes) {
        int i;
        int result;

        i = 0;
        result = 0;

        while (i < nodes.length) {
            if (nodes[i] instanceof SpecialNode) {
                result = result + 1;
            }
            i = i + 1;
        }

        return result;
    }

    public Node firstNonNull(Node[] nodes) {
        int i;

        i = 0;
        while (i < nodes.length) {
            if (nodes[i] != null) {
                return nodes[i];
            }
            i = i + 1;
        }

        return null;
    }
}

class Node {
    int value;
    Node next;

    public Node(int value) {
        this.value = value;
        this.next = null;
    }
}

class SpecialNode extends Node {
    int specialValue;

    public SpecialNode(int value, int specialValue) {
        super(value);
        this.specialValue = specialValue;
    }
}