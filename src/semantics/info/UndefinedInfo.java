package semantics.info;

public class UndefinedInfo extends Info {
    private static final UndefinedInfo instance = new UndefinedInfo();

    public static UndefinedInfo getInstance() {
        return instance;
    }

    private UndefinedInfo() {
        super("undefined");
    }
}
