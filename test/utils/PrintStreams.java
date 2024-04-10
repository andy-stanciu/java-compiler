import java.util.Objects;

/***
 * Container encapsulating the contents printed to system out and system err.
 */
public final class PrintStreams {
    private final String systemOut;
    private final String systemErr;

    /**
     * @param systemOut contents printed to system out.
     * @param systemErr contents printed to system err.
     */
    public PrintStreams(String systemOut, String systemErr) {
        this.systemOut = systemOut;
        this.systemErr = systemErr;
    }

    public String systemOut() {
        return systemOut;
    }

    public String systemErr() {
        return systemErr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PrintStreams) obj;
        return Objects.equals(this.systemOut, that.systemOut) &&
                Objects.equals(this.systemErr, that.systemErr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemOut, systemErr);
    }

    @Override
    public String toString() {
        return "PrintStreams[" +
                "systemOut=" + systemOut + ", " +
                "systemErr=" + systemErr + ']';
    }
}
