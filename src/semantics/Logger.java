package semantics;

public final class Logger {
    private String sourceFile;
    private int lineNumber;
    private int errorCount;
    private int warningCount;

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
        this.errorCount = 0;
        this.warningCount = 0;
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
        return errorCount > 0;
    }

    /**
     * @return The number of errors.
     */
    public int getErrorCount() {
        return errorCount;
    }

    /**
     * @return Whether the logger has logged a warning.
     */
    public boolean hasWarnings() {
        return warningCount > 0;
    }

    /**
     * @return The number of warnings.
     */
    public int getWarningCount() {
        return warningCount;
    }

    /**
     * Logs an error to stderr.
     * @param message The message to log.
     * @param args Format args.
     */
    public void logError(String message, Object... args) {
        System.err.printf("ERROR @ " + sourceFile + ":" + lineNumber + ": " + message, args);
        errorCount++;
    }

    /**
     * Logs a warning to stderr.
     * @param message The message to log.
     * @param args Format args.
     */
    public void logWarning(String message, Object args) {
        System.err.printf("WARNING @ " + sourceFile + ":" + lineNumber + ": " + message, args);
        warningCount++;
    }
}
