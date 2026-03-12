package mutate4java;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

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
