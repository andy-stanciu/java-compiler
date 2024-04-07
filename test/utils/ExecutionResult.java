import java.util.Objects;

/***
 * Container encapsulating an exit status code and
 * the contents printed to system out and system err.
 */
public final class ExecutionResult {
    private final int exitStatus;
    private final String systemOut;
    private final String systemErr;

    /**
     * @param exitStatus the exit status code.
     * @param systemOut  contents printed to system out.
     * @param systemErr  contents printed to system err.
     */
    public ExecutionResult(int exitStatus,
                           String systemOut, String systemErr) {
        this.exitStatus = exitStatus;
        this.systemOut = systemOut;
        this.systemErr = systemErr;
    }

    ExecutionResult(int exitStatus, PrintStreams printStreams) {
        this(exitStatus, printStreams.systemOut(), printStreams.systemErr());
    }

    public int exitStatus() {
        return exitStatus;
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
        var that = (ExecutionResult) obj;
        return this.exitStatus == that.exitStatus &&
                Objects.equals(this.systemOut, that.systemOut) &&
                Objects.equals(this.systemErr, that.systemErr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exitStatus, systemOut, systemErr);
    }

    @Override
    public String toString() {
        return "ExecutionResult[" +
                "exitStatus=" + exitStatus + ", " +
                "systemOut=" + systemOut + ", " +
                "systemErr=" + systemErr + ']';
    }
}
