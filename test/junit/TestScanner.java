import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestScanner {
    public static final String TEST_FILES_LOCATION = "test/resources/Scanner/";
    public static final String TEST_FILES_INPUT_EXTENSION = ".java";
    public static final String TEST_FILES_EXPECTED_EXTENSION = ".expected";
    public static final String TEST_FILES_ERROR_EXTENSION = ".err";

    private void runScannerSuccessTestCase(String name) {
        try {
            new MiniJavaTestBuilder()
                    .assertSystemOutMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_EXPECTED_EXTENSION))
                    .assertSystemErrIsEmpty()
                    .assertExitSuccess()
                    .testCompiler("-S",TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private void runScannerFailTestCase(String name) {
        try {
            new MiniJavaTestBuilder()
                    .assertSystemOutMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_EXPECTED_EXTENSION))
                    .assertSystemErrMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_ERROR_EXTENSION))
                    .assertExitFailure()
                    .testCompiler("-S",TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTrickyComments() {
        runScannerSuccessTestCase("TrickyComments");
    }

    @Test
    public void testBrokenComments() {
        runScannerFailTestCase("BrokenComments");
    }

    @Test
    public void testFactorialBroken() {
        runScannerFailTestCase("FactorialBroken");
    }

    @Test
    public void testBinarySearch() {
        runScannerSuccessTestCase("BinarySearch");
    }

    @Test
    public void testBinaryTree() {
        runScannerSuccessTestCase("BinaryTree");
    }

    @Test
    public void testBubbleSort() {
        runScannerSuccessTestCase("BubbleSort");
    }

    @Test
    public void testFactorial() {
        runScannerSuccessTestCase("Factorial");
    }

    @Test
    public void testLinearSearch() {
        runScannerSuccessTestCase("LinearSearch");
    }

    @Test
    public void testLinkedList() {
        runScannerSuccessTestCase("LinkedList");
    }

    @Test
    public void testQuickSort() {
        runScannerSuccessTestCase("QuickSort");
    }

    @Test
    public void testTreeVisitor() {
        runScannerSuccessTestCase("TreeVisitor");
    }
}
