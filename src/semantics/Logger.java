package semantics;

public final class Logger {
    private String sourceFile;
    private int lineNumber;
    private boolean error;

    private static Logger instance;

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    private Logger() {}

    /**
     * Initializes the logger.
     * @param sourceFile The source file.
     */
    public void start(String sourceFile) {
        this.sourceFile = sourceFile;
        this.lineNumber = 0;
        this.error = false;
    }

    /**
     * Sets the logger's current line number.
     * @param lineNumber The line number.
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return Whether the logger has logged an error.
     */
    public boolean hasError() {
        return error;
    }

    /**
     * Logs an error to stderr.
     * @param message The message to log.
     * @param args Format args.
     */
    public void logError(String message, Object... args) {
        System.err.printf("Error @ " + sourceFile + ":" + lineNumber + ": " + message, args);
        error = true;
    }

    /**
     * Logs a warning to stderr.
     * @param message The message to log.
     * @param args Format args.
     */
    public void logWarning(String message, Object args) {
        System.err.printf("Warning @ " + sourceFile + ":" + lineNumber + ": " + message, args);
    }
}
