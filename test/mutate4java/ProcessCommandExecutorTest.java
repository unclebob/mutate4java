package mutate4java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessCommandExecutorTest {

    @TempDir
    Path tempDir;

    @Test
    void capturesSuccessfulCommandOutput() throws Exception {
        CommandResult result = new ProcessCommandExecutor().run(
                List.of("sh", "-c", "printf 'ok'"),
                tempDir
        );

        assertEquals(0, result.exitCode());
        assertEquals("ok", result.output());
        assertFalse(result.timedOut());
    }

    @Test
    void returnsTimeoutExitCodeWhenCommandTakesTooLong() throws Exception {
        CommandResult result = new ProcessCommandExecutor().run(
                List.of("sh", "-c", "sleep 1"),
                tempDir,
                10
        );

        assertEquals(124, result.exitCode());
        assertTrue(result.timedOut());
    }
}
