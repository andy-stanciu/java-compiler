import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * A helper for creating and running JUnit tests testing the Java compiler.
 * <p><br>
 * The JavaTestBuilder allows asserting the output and error produced
 * when running Java's main method, as well as its exit status when
 * it finishes processing the specified Java source code:
 *
 * <pre>{@code
 * new JavaTestBuilder()
 *         .assertSystemOutMatchesContentsOf(Path.of("test/resources/", "Example.expected.out"))
 *         .assertSystemErrIsEmpty()
 *         .assertExitSuccess()
 *         .testCompiler("-S", "test/resources/Example.java");
 * }</pre>
 * <p>
 * The above example asserts that, when scanning "Example.java" from the
 * test resources folder,
 *
 * <ol>
 *     <li>
 *         {@code System.out} matches the contents of "Example.expected.out"
 *         in the "test/resources" folder
 *     </li>
 *     <li>{@code System.err} is empty, and</li>
 *     <li>it exited with status 0.</li>
 * </ol>
 * <p>
 * In general, to assert on {@code System.out}, use
 * <ul>
 *     <li>{@link #assertSystemOutMatchesContentsOf(Path)},</li>
 *     <li>{@link #assertSystemOutMatches(String)}, or</li>
 *     <li>{@link #assertSystemOutIsEmpty()}.</li>
 * </ul>
 * <p>
 * Similarly, to assert on {@code System.err}, use
 * <ul>
 *     <li>{@link #assertSystemErrMatchesContentsOf(Path)},</li>
 *     <li>{@link #assertSystemErrMatches(String)}, or</li>
 *     <li>{@link #assertSystemErrIsEmpty()}.</li>
 * </ul>
 * <p>
 * To assert on the exit status, use either of
 * <ul>
 *     <li>{@link #assertExitSuccess()}</li>
 *     <li>{@link #assertExitFailure()}</li>
 * </ul>
 * <p>
 * For advanced customizations/troubleshooting, see the course website on
 * how to use the test utilities and setup requirements to address:
 *
 * <pre>java.lang.AssertionError: need to add -Djava.security.manager=allow to VM options.</pre>
 *
 * @author Apollo Zhu
 */
public class JavaTestBuilder {
    private final List<Consumer<ExecutionResult>> assertions = new ArrayList<>();

    /**
     * Constructs a {@code JavaTestBuilder}.
     */
    public JavaTestBuilder() {
    }

    public JavaTestBuilder assertExecutionResult(Consumer<ExecutionResult> consumer) {
        assertions.add(consumer);
        return this;
    }

    /**
     * Performs custom assertions on the contents of {@code System.out}
     * after running Java's main method, if none of
     * {@link #assertSystemOutMatchesContentsOf(Path)},
     * {@link #assertSystemOutMatches(String)}, or
     * {@link #assertSystemOutIsEmpty()} performs the desired assertion.
     *
     * @param consumer the code that checks the contents of {@code System.out}.
     * @return this.
     * @see #assertSystemOutMatchesContentsOf(Path)
     * @see #assertSystemOutMatches(String)
     * @see #assertSystemOutIsEmpty()
     */
    public JavaTestBuilder assertSystemOut(Consumer<String> consumer) {
        return assertExecutionResult(executionResult
                -> consumer.accept(executionResult.systemOut()));
    }

    /**
     * Asserts that {@code System.out} will match the given String
     * after running Java's main method.
     *
     * @param expectedSystemOut the expected contents of {@code System.out}.
     * @return this.
     * @see #assertSystemOutMatchesContentsOf(Path)
     * @see #assertSystemOutIsEmpty()
     * @see #assertSystemOut(Consumer)
     */
    public JavaTestBuilder assertSystemOutMatches(String expectedSystemOut) {
        return assertSystemOutMatches("System.out doesn't match.", expectedSystemOut);
    }

    /**
     * Asserts that {@code System.out} will match the given String
     * after running Java's main method.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @param expectedSystemOut the expected contents of {@code System.out}.
     * @return this.
     * @see #assertSystemOutMatchesContentsOf(Path)
     * @see #assertSystemOutIsEmpty()
     * @see #assertSystemOut(Consumer)
     */
    public JavaTestBuilder assertSystemOutMatches(String message, String expectedSystemOut) {
        return assertSystemOut(actualSystemOut
                -> assertEquals(message,
                expectedSystemOut.trim().replaceAll("\\r\\n", "\n"),
                actualSystemOut.trim().replaceAll("\\r\\n", "\n")));
    }

    /**
     * Asserts that {@code System.out} will match the contents of the given file
     * after running Java's main method.
     *
     * @param file the file containing the expected contents of
     *             {@code System.out}. This method will attempt to fix this path
     *             if the JUnit test is ran through IntelliJ IDEA, so the test
     *             can succeed as it would when ran through {@code ant}.
     * @return this.
     * @throws IOException if the specified file can't be read from.
     * @see #assertSystemOutMatches(String)
     * @see #assertSystemOutIsEmpty()
     * @see #assertSystemOut(Consumer)
     */
    public JavaTestBuilder assertSystemOutMatchesContentsOf(Path file) throws IOException {
        return assertSystemOutMatches(Files.readString(TestUtils._fixPath(file)));
    }

    /**
     * Asserts that {@code System.out} will match the contents of the given file
     * after running Java's main method.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @param file the file containing the expected contents of
     *             {@code System.out}. This method will attempt to fix this path
     *             if the JUnit test is ran through IntelliJ IDEA, so the test
     *             can succeed as it would when ran through {@code ant}.
     * @return this.
     * @throws IOException if the specified file can't be read from.
     * @see #assertSystemOutMatches(String)
     * @see #assertSystemOutIsEmpty()
     * @see #assertSystemOut(Consumer)
     */
    public JavaTestBuilder assertSystemOutMatchesContentsOf(String message, Path file) throws IOException {
        return assertSystemOutMatches(message, Files.readString(TestUtils._fixPath(file)));
    }

    /**
     * Asserts that {@code System.out} will be empty
     * after running Java's main method.
     *
     * @return this.
     * @see #assertSystemOutMatchesContentsOf(Path)
     * @see #assertSystemOutMatches(String)
     * @see #assertSystemOut(Consumer)
     */
    public JavaTestBuilder assertSystemOutIsEmpty() {
        return assertSystemOutMatches("");
    }

    /**
     * Asserts that {@code System.out} will be empty
     * after running Java's main method.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @return this.
     * @see #assertSystemOutMatchesContentsOf(Path)
     * @see #assertSystemOutMatches(String)
     * @see #assertSystemOut(Consumer)
     */
    public JavaTestBuilder assertSystemOutIsEmpty(String message) {
        return assertSystemOutMatches(message, "");
    }

    /**
     * Performs custom assertions on the contents of {@code System.err}
     * after running Java's main method, if none of
     * {@link #assertSystemErrMatchesContentsOf(Path)},
     * {@link #assertSystemErrMatches(String)}, or
     * {@link #assertSystemErrIsEmpty()} performs the desired assertion.
     *
     * @param consumer the code that checks the contents of {@code System.err}.
     * @return this.
     * @see #assertSystemErrMatchesContentsOf(Path)
     * @see #assertSystemErrMatches(String)
     * @see #assertSystemErrIsEmpty()
     */
    public JavaTestBuilder assertSystemErr(Consumer<String> consumer) {
        return assertExecutionResult(executionResult
                -> consumer.accept(executionResult.systemErr()));
    }

    /**
     * Asserts that {@code System.err} will match the given String
     * after running Java's main method.
     *
     * @param expectedSystemErr the expected contents of {@code System.err}.
     * @return this.
     * @see #assertSystemErrMatchesContentsOf(Path)
     * @see #assertSystemErrIsEmpty()
     * @see #assertSystemErr(Consumer)
     */
    public JavaTestBuilder assertSystemErrMatches(String expectedSystemErr) {
        return assertSystemErrMatches("System.err doesn't match.", expectedSystemErr);
    }

    /**
     * Asserts that {@code System.err} will match the given String
     * after running Java's main method.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @param expectedSystemErr the expected contents of {@code System.err}.
     * @return this.
     * @see #assertSystemErrMatchesContentsOf(Path)
     * @see #assertSystemErrIsEmpty()
     * @see #assertSystemErr(Consumer)
     */
    public JavaTestBuilder assertSystemErrMatches(String message, String expectedSystemErr) {
        return assertSystemErr(actualSystemErr 
                -> assertEquals(message,
                expectedSystemErr.trim().replaceAll("\\r\\n", "\n"),
                actualSystemErr.trim().replaceAll("\\r\\n", "\n")));
    }

    /**
     * Asserts that {@code System.err} will match the contents of the given file
     * after running Java's main method.
     *
     * @param file the file containing the expected contents of
     *             {@code System.out}. This method will attempt to fix this path
     *             if the JUnit test is ran through IntelliJ IDEA, so the test
     *             can succeed as it would when ran through {@code ant}.
     * @return this.
     * @throws IOException if the specified file can't be read from.
     * @see #assertSystemErrMatches(String)
     * @see #assertSystemErrIsEmpty()
     * @see #assertSystemErr(Consumer)
     */
    public JavaTestBuilder assertSystemErrMatchesContentsOf(Path file) throws IOException {
        return assertSystemErrMatches(Files.readString(TestUtils._fixPath(file)));
    }

    /**
     * Asserts that {@code System.err} will match the contents of the given file
     * after running Java's main method.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @param file the file containing the expected contents of
     *             {@code System.out}. This method will attempt to fix this path
     *             if the JUnit test is ran through IntelliJ IDEA, so the test
     *             can succeed as it would when ran through {@code ant}.
     * @return this.
     * @throws IOException if the specified file can't be read from.
     * @see #assertSystemErrMatches(String)
     * @see #assertSystemErrIsEmpty()
     * @see #assertSystemErr(Consumer)
     */
    public JavaTestBuilder assertSystemErrMatchesContentsOf(String message, Path file) throws IOException {
        return assertSystemErrMatches(message, Files.readString(TestUtils._fixPath(file)));
    }

    /**
     * Asserts that {@code System.err} will be empty
     * after running Java's main method.
     *
     * @return this.
     * @see #assertSystemErrMatchesContentsOf(Path)
     * @see #assertSystemErrMatches(String)
     * @see #assertSystemErr(Consumer)
     */
    public JavaTestBuilder assertSystemErrIsEmpty() {
        return assertSystemErrMatches("");
    }

    /**
     * Asserts that {@code System.err} will be empty
     * after running Java's main method.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @return this.
     * @see #assertSystemErrMatchesContentsOf(Path)
     * @see #assertSystemErrMatches(String)
     * @see #assertSystemErr(Consumer)
     */
    public JavaTestBuilder assertSystemErrIsEmpty(String message) {
        return assertSystemErrMatches(message, "");
    }

    /**
     * Asserts that Java's main method will exit with the given status code.
     *
     * @param exitStatus the expected status passed to {@code System.exit}.
     * @return this.
     */
    public JavaTestBuilder assertExitsWith(int exitStatus) {
        return assertExitsWith("exit status doesn't match.", exitStatus);
    }

    /**
     * Asserts that Java's main method will exit with the given status code.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @param exitStatus the expected status passed to {@code System.exit}.
     * @return this.
     */
    public JavaTestBuilder assertExitsWith(String message, int exitStatus) {
        return assertExecutionResult(executionResult -> {
            assertEquals(message, exitStatus, executionResult.exitStatus());
        });
    }

    /**
     * Asserts that Java's main method will exit with status code 0.
     *
     * @return this.
     */
    public JavaTestBuilder assertExitSuccess() {
        return assertExitsWith(0);
    }

    /**
     * Asserts that Java's main method will exit with status code 0.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @return this.
     */
    public JavaTestBuilder assertExitSuccess(String message) {
        return assertExitsWith(message, 0);
    }

    /**
     * Asserts that Java's main method will exit with status code 1.
     *
     * @return this.
     */
    public JavaTestBuilder assertExitFailure() {
        return assertExitsWith(1);
    }

    /**
     * Asserts that Java's main method will exit with status code 1.
     *
     * @param message the identifying message for the {@code AssertionError}
     * @return this.
     */
    public JavaTestBuilder assertExitFailure(String message) {
        return assertExitsWith(message, 1);
    }

    /**
     * Runs the Java compiler's main method with the given arguments and
     * performs the assertions specified earlier.
     *
     * @param args the arguments to pass to the Java compiler. This method
     *             will attempt to fix path to ".java" files specified in this
     *             array if the JUnit test is ran through IntelliJ IDEA, so the
     *             test can succeed as it would when ran through {@code ant}.
     */
    public void testCompiler(String... args) {
        String[] actualArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            // potentially fix path for IntelliJ if ends with .java
            if (arg.endsWith(".java")) {
                String fixed = TestUtils
                        ._fixPath(Path.of(arg)).toString();
                if (!fixed.equals(arg)) {
                    actualArgs[i] = fixed;
                    continue;
                }
            }
            actualArgs[i] = arg;
        }
        ExecutionResult result = TestUtils
                .runCatchingExit(() -> Java.main(actualArgs));
        for (Consumer<ExecutionResult> assertion : assertions) {
            assertion.accept(result);
        }
    }

    /**
     * Compiles the specified Java source file using both the Java and the
     * Java compiler and asserts that running either version of the compiled
     * Java program will exit with success and prints the same output to
     * both {@code System.out} and {@code System.err} respectively.
     * <p>
     * Since the family of {@link #assertSystemOut} and
     * {@link #assertSystemErr} assertions are asserting on the contents printed
     * directly by the Java compiler, the correct assertions are:
     *
     * <ul>
     *     <li>
     *         asserting that {@code System.out} matches the desired
     *         assembly code
     *     </li>
     *     <li>{@link #assertSystemErrIsEmpty()}, and</li>
     *     <li>{@link #assertExitSuccess()}.</li>
     * </ul>
     *
     * @param pathToJavaSourceFile       path to the Java source code to
     *                                       be compiled by both Java and the
     *                                       Java compiler. This method will
     *                                       attempt to fix this path if the
     *                                       JUnit test is ran through IntelliJ
     *                                       IDEA, so the test can succeed as it
     *                                       would when ran through {@code ant}.
     * @param argsForCompiledJavaProgram arguments to pass to the compiled
     *                                       Java program.
     * @throws IOException          if the given Java source file
     *                              can't be read.
     * @throws InterruptedException if either Java, the Java compiler,
     *                              gcc, or the compiled program didn't finish
     *                              running in reasonable time.
     * @see TestUtils#EXEC_WAIT_TIMEOUT_SECONDS
     */
    public void testCompiledProgramOutputMatchesJava(
            Path pathToJavaSourceFile,
            String... argsForCompiledJavaProgram
    ) throws IOException, InterruptedException {
        assertTrue("assertion are not allowed when using testCompiledProgramOutputMatchesJava",
                assertions.isEmpty());

        Path path = TestUtils._fixPath(pathToJavaSourceFile);
        ExecutionResult expectedResult = TestUtils
                .compileAndRunWithJava(path, argsForCompiledJavaProgram);
        assertEquals("failed to compile and run with Java.",
                "", expectedResult.systemErr());
        assertEquals("failed to compile and run with Java.",
                0, expectedResult.exitStatus());
        ExecutionResult result = TestUtils
                .compileAndRunWithCompiler(path, argsForCompiledJavaProgram);
        // Note that here we use "standard output" instead of "System.out" to
        // signify that this describing the behavior of the compiled program,
        // not the Java compiler itself.
        assertEquals("standard output doesn't match Java.",
                expectedResult.systemOut(), result.systemOut());
        // It's unlikely for extension project to implement the entire Throwable
        // system during a quarter, so we can probably assert that the compiled
        // program exits with status 0 and throws no error.
        assertEquals("standard error doesn't match Java.",
                expectedResult.systemErr(), result.systemErr());
        assertEquals("compiled program should exit with success.",
                0, result.exitStatus());
    }
}
