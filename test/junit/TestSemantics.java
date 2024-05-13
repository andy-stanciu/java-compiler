import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestSemantics {
    public static final String TEST_FILES_LOCATION = "test/resources/Semantics/";
    public static final String TEST_FILES_INPUT_EXTENSION = ".java";
    public static final String TEST_FILES_TABLE_EXTENSION = ".tbl";
    public static final String TEST_FILES_ERROR_EXTENSION = ".err";

    private void runSemanticsSuccessTestCase(String name) {
        try {
            new MiniJavaTestBuilder()
                    .assertSystemOutMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_TABLE_EXTENSION))
                    .assertSystemErrIsEmpty()
                    .assertExitSuccess()
                    .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private void runSemanticsFailTestCase(String name) {
        try {
            new MiniJavaTestBuilder()
                    .assertSystemOutMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_TABLE_EXTENSION))
                    .assertExitFailure()
                    .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
            new MiniJavaTestBuilder()
                    .assertSystemErrMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_ERROR_EXTENSION))
                    .assertExitFailure()
                    .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCrazyInheritance() {
        runSemanticsSuccessTestCase("CrazyInheritance");
    }

    @Test
    public void testHal() {
        runSemanticsSuccessTestCase("Hal");
    }

    @Test
    public void testInheritance1() {
        runSemanticsSuccessTestCase("Inheritance1");
    }

    @Test
    public void testInheritance2() {
        runSemanticsSuccessTestCase("Inheritance2");
    }

    @Test
    public void testUndefined1Fail() {
        runSemanticsFailTestCase("Undefined1Fail");
    }

    @Test
    public void testBinarySearch() {
        runSemanticsSuccessTestCase("BinarySearch");
    }

    @Test
    public void testBinaryTree() {
        runSemanticsSuccessTestCase("BinaryTree");
    }

    @Test
    public void testBubbleSort() {
        runSemanticsSuccessTestCase("BubbleSort");
    }

    @Test
    public void testFactorial() {
        runSemanticsSuccessTestCase("Factorial");
    }

    @Test
    public void testFooFail() {
        runSemanticsFailTestCase("FooFail");
    }

    @Test
    public void testLinearSearch() {
        runSemanticsSuccessTestCase("LinearSearch");
    }

    @Test
    public void testLinkedList() {
        runSemanticsSuccessTestCase("LinkedList");
    }

    @Test
    public void testQuickSort() {
        runSemanticsSuccessTestCase("QuickSort");
    }

    @Test
    public void testTreeVisitor() {
        runSemanticsSuccessTestCase("TreeVisitor");
    }
}