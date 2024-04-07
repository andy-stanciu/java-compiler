import java.security.Permission;

// No replacement for intercepting exit yet.
// https://bugs.openjdk.org/browse/JDK-8199704
// Have to name this {@code allow} to be backward compatible.
@SuppressWarnings("removal")
public final class allow extends SecurityManager {
    private boolean isActive = false;

    public allow() {
    }

    /**
     * Setting -Djava.security.manager=allow before Java 12 will set this class
     * as the default security manager, meaning normal JUnit exit will also
     * trigger checkExit. Therefore, we should only activate when requested.
     */
    public void activate() {
        isActive = true;
    }

    @Override
    public void checkExit(int status) {
        if (isActive) {
            throw new ExitException(status);
        }
    }

    @Override
    public void checkPermission(Permission perm) {
        // allow
    }

    public static final class ExitException extends SecurityException {
        private final int exitStatus;

        ExitException(int exitStatus) {
            this.exitStatus = exitStatus;
        }

        public int getExitStatus() {
            return exitStatus;
        }
    }
}