import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestCodeGen {
    public static final String TEST_FILES_LOCATION = "test/resources/CodeGen/";
    public static final String TEST_FILES_INPUT_EXTENSION = ".java";

    private void runCompilerTest(String name) {
        try {
            new MiniJavaTestBuilder()
                    .testCompiledProgramOutputMatchesJava(
                            Path.of(TEST_FILES_LOCATION, name + TEST_FILES_INPUT_EXTENSION));
        } catch (IOException | InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFlow1() {
        runCompilerTest("Flow1");
    }
}
