package semantics;

/**
 * Should only be thrown for fatal semantics-related exceptions.
 */
public final class IllegalSemanticException extends RuntimeException {
    public IllegalSemanticException(String message) {
        super(message);
    }
}
