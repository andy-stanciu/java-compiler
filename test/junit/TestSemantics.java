import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestSemantics {
    private static final boolean OVERWRITE_TABLE_OUTPUT = false;

    private static final String TEST_FILES_LOCATION = "test/resources/Semantics/";
    private static final String TEST_FILES_INPUT_EXTENSION = ".java";
    private static final String TEST_FILES_TABLE_EXTENSION = ".tbl";
    private static final String TEST_FILES_ERROR_EXTENSION = ".err";

    private void runSemanticsSuccessTestCase(String name) {
        try {
            final Path expected = Path.of(TEST_FILES_LOCATION, name + TEST_FILES_TABLE_EXTENSION);
            if (OVERWRITE_TABLE_OUTPUT) {
                new JavaTestBuilder()
                        .overwriteExpectedSystemOutWithContentsOf(expected)
                        .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
            }
            new JavaTestBuilder()
                    .assertSystemOutMatchesContentsOf(expected)
                    .assertSystemErrIsEmpty()
                    .assertExitSuccess()
                    .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private void runSemanticsFailTestCase(String name) {
        try {
            final Path expected = Path.of(TEST_FILES_LOCATION, name + TEST_FILES_TABLE_EXTENSION);
            if (OVERWRITE_TABLE_OUTPUT) {
                new JavaTestBuilder()
                        .overwriteExpectedSystemOutWithContentsOf(expected)
                        .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
            }
            new JavaTestBuilder()
                    .assertSystemOutMatchesContentsOf(expected)
                    .assertExitFailure()
                    .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
            new JavaTestBuilder()
                    .assertSystemErrMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_ERROR_EXTENSION))
                    .assertExitFailure()
                    .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testComplexAssign() {
        runSemanticsSuccessTestCase("ComplexAssign");
    }

    @Test
    public void testAmbiguousConstructorsFail() {
        runSemanticsFailTestCase("AmbiguousConstructorsFail");
    }

    @Test
    public void testSuperFail() {
        runSemanticsFailTestCase("SuperFail");
    }

    @Test
    public void testSuper() {
        runSemanticsSuccessTestCase("Super");
    }

    @Test
    public void testNewExpressions() {
        runSemanticsSuccessTestCase("NewExpressions");
    }

    @Test
    public void testAllErrorsFail() {
        runSemanticsFailTestCase("AllErrorsFail");
    }

    @Test
    public void testThisCtorFail() {
        runSemanticsFailTestCase("ThisCtorFail");
    }

    @Test
    public void testConflictsFail() {
        runSemanticsFailTestCase("ConflictsFail");
    }

    @Test
    public void testCyclicFail() {
        runSemanticsFailTestCase("CyclicFail");
    }

    @Test
    public void testUndeclaredRepeatFail() {
        runSemanticsFailTestCase("UndeclaredRepeatFail");
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

    @Test
    public void testVoidFail() {
        runSemanticsFailTestCase("VoidFail");
    }

    @Test
    public void testForFail() {
        runSemanticsFailTestCase("ForFail");
    }
}