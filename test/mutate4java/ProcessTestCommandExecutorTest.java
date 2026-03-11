package mutate4java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessTestCommandExecutorTest {

    @TempDir
    Path tempDir;

    @Test
    void capturesSuccessfulTestRunOutput() throws Exception {
        FakeProcess process = FakeProcess.completed(0, "ok");

        TestRun result = new ProcessTestCommandExecutor(projectRoot -> process).runTests(tempDir, 0);

        assertEquals(0, result.exitCode());
        assertEquals("ok", result.output());
        assertFalse(result.timedOut());
    }

    @Test
    void returnsTimeoutExitCodeWhenTestRunTakesTooLong() throws Exception {
        FakeProcess process = FakeProcess.timedOut("slow");

        TestRun result = new ProcessTestCommandExecutor(projectRoot -> process).runTests(tempDir, 10);

        assertEquals(124, result.exitCode());
        assertTrue(result.timedOut());
        assertTrue(process.destroyed);
    }

    @Test
    void returnsEmptyOutputWhenReadingProcessOutputFails() throws Exception {
        FakeProcess process = FakeProcess.completed(0, "ignored");
        process.inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("boom");
            }
        };

        TestRun result = new ProcessTestCommandExecutor(projectRoot -> process).runTests(tempDir, 0);

        assertEquals("", result.output());
    }

    @Test
    void startsConfiguredCommandInTargetDirectory() throws Exception {
        TestRun result = new ProcessTestCommandExecutor(List.of("sh", "-c", "printf ok")).runTests(tempDir, 0);

        assertEquals(0, result.exitCode());
        assertEquals("ok", result.output());
        assertFalse(result.timedOut());
    }

    private static final class FakeProcess extends Process {
        private final int exitCode;
        private final boolean waitResult;
        private InputStream inputStream;
        private boolean destroyed;

        private FakeProcess(int exitCode, boolean waitResult, String output) {
            this.exitCode = exitCode;
            this.waitResult = waitResult;
            this.inputStream = new ByteArrayInputStream(output.getBytes());
        }

        private static FakeProcess completed(int exitCode, String output) {
            return new FakeProcess(exitCode, true, output);
        }

        private static FakeProcess timedOut(String output) {
            return new FakeProcess(7, false, output);
        }

        @Override
        public OutputStream getOutputStream() {
            return OutputStream.nullOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public InputStream getErrorStream() {
            return InputStream.nullInputStream();
        }

        @Override
        public int waitFor() {
            return exitCode;
        }

        @Override
        public boolean waitFor(long timeout, java.util.concurrent.TimeUnit unit) {
            return waitResult;
        }

        @Override
        public int exitValue() {
            return exitCode;
        }

        @Override
        public Process destroyForcibly() {
            destroyed = true;
            return this;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }

        @Override
        public boolean isAlive() {
            return false;
        }
    }
}
