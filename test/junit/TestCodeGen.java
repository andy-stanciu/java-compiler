import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestCodeGen {
    public static final String TEST_FILES_LOCATION = "test/resources/CodeGen/";

    private void runCompilerTest(String name) {
        try {
            new MiniJavaTestBuilder()
                    .testCompiledProgramOutputMatchesJava(
                            Path.of(TEST_FILES_LOCATION, name));
        } catch (IOException | InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAll() {
        var path = CSE401TestUtils._fixPath(Path.of(TEST_FILES_LOCATION));
        var sourceDirectory = path.toFile();
        for (var file : sourceDirectory.listFiles()) {
            if (file.isFile()) {
                runCompilerTest(file.getName());
            }
        }
    }
}
