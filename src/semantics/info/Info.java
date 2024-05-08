package semantics.info;

public abstract class Info implements Comparable<Info> {
    public final String name;

    public Info(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Info other) {
        return name.compareTo(other.name);
    }
}
