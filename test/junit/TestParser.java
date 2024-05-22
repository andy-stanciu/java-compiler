import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestParser {
    public static final String TEST_FILES_LOCATION = "test/resources/Parser/";
    public static final String TEST_FILES_INPUT_EXTENSION = ".java";
    public static final String TEST_FILES_AST_EXTENSION = ".ast";
    public static final String TEST_FILES_PRETTY_EXTENSION = ".pretty";

    private void runParserSuccessTestCase(String name) {
        try {
            new MiniJavaTestBuilder()
                    .assertSystemOutMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_AST_EXTENSION))
                    .assertSystemErrIsEmpty()
                    .assertExitSuccess()
                    .testCompiler("-A", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
            new MiniJavaTestBuilder()
                    .assertSystemOutMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_PRETTY_EXTENSION))
                    .assertSystemErrIsEmpty()
                    .assertExitSuccess()
                    .testCompiler("-P", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private void runParserFailTestCase(String name) {
        new MiniJavaTestBuilder()
                .assertExitFailure()
                .testCompiler("-A", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
    }

    @Test
    public void testMainBroken() {
        runParserFailTestCase("MainBroken");
    }

    @Test
    public void testAssignBroken() {
        runParserFailTestCase("AssignBroken");
    }

    @Test
    public void testNonsense() {
        runParserSuccessTestCase("Nonsense");
    }

    @Test
    public void testPrecedence() {
        runParserSuccessTestCase("Precedence");
    }

    @Test
    public void testBinarySearch() {
        runParserSuccessTestCase("BinarySearch");
    }

    @Test
    public void testBinaryTree() {
        runParserSuccessTestCase("BinaryTree");
    }

    @Test
    public void testBubbleSort() {
        runParserSuccessTestCase("BubbleSort");
    }

    @Test
    public void testFactorial() {
        runParserSuccessTestCase("Factorial");
    }

    @Test
    public void testFoo() {
        runParserSuccessTestCase("Foo");
    }

    @Test
    public void testLinearSearch() {
        runParserSuccessTestCase("LinearSearch");
    }

    @Test
    public void testLinkedList() {
        runParserSuccessTestCase("LinkedList");
    }

    @Test
    public void testQuickSort() {
        runParserSuccessTestCase("QuickSort");
    }

    @Test
    public void testTreeVisitor() {
        runParserSuccessTestCase("TreeVisitor");
    }
}
