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
                    .assertSystemErrMatchesContentsOf(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_ERROR_EXTENSION))
                    .assertExitFailure()
                    .testCompiler("-T", TEST_FILES_LOCATION + name + TEST_FILES_INPUT_EXTENSION);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
