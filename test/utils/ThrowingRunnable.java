/**
 * A potentially throwing piece of code to be passed to {@code CSE401TestUtils}
 * testing utility methods.
 * <p><br>
 * To pass your code to the utility methods, you can wrap your code like this
 * to make it a {@code ThrowingRunnable}:
 *
 * <pre>
 * () -&gt; {
 *     ... your code goes here ...
 * }
 * </pre>
 *
 * @param <Thrown> the type of the error or exception that can
 *                 potentially be thrown by the code to be run.
 */
@FunctionalInterface
public interface ThrowingRunnable<Thrown extends Throwable> {
    /**
     * The code to be run.
     *
     * @throws Thrown some throwable error or exception the implementation
     *                decides to throw, if any.
     */
    void run() throws Thrown;
}
