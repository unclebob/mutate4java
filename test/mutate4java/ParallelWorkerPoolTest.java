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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParallelWorkerPoolTest {

    @TempDir
    Path tempDir;

    @Test
    void rethrowsCheckedCauseFromWorkerFailure() throws Exception {
        Path workerRoot = tempDir.resolve("worker");
        Path sourceFile = workerRoot.resolve("src/mutate4java/Sample.java");
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, "class Sample { boolean value() { return true; } }");

        MutationSite site = new MutationSite(
                sourceFile,
                1,
                40,
                44,
                "true",
                "false",
                "replace true with false"
        );
        MutationJob job = new MutationJob(site, workerRoot.relativize(sourceFile), 1_000L, 0, 1);
        IOException expected = new IOException("boom");

        try (ParallelWorkerPool pool = new ParallelWorkerPool(
                List.of(workerRoot),
                new FailingExecutor(expected),
                new NoOpProgressReporter()
        )) {
            IOException thrown = assertThrows(IOException.class, () -> pool.runAll(List.of(job)));
            assertSame(expected, thrown);
        }
    }

    @Test
    void wrapsNonExceptionCauseFromWorkerFailure() throws Exception {
        Path workerRoot = tempDir.resolve("worker");
        Path sourceFile = workerRoot.resolve("src/mutate4java/Sample.java");
        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, "class Sample { boolean value() { return true; } }");

        MutationSite site = new MutationSite(
                sourceFile,
                1,
                40,
                44,
                "true",
                "false",
                "replace true with false"
        );
        MutationJob job = new MutationJob(site, workerRoot.relativize(sourceFile), 1_000L, 0, 1);

        try (ParallelWorkerPool pool = new ParallelWorkerPool(
                List.of(workerRoot),
                new ErrorExecutor(),
                new NoOpProgressReporter()
        )) {
            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> pool.runAll(List.of(job)));
            assertInstanceOf(AssertionError.class, thrown.getCause());
        }
    }

    private static final class FailingExecutor implements TestCommandExecutor {
        private final IOException failure;

        private FailingExecutor(IOException failure) {
            this.failure = failure;
        }

        @Override
        public TestRun runTests(Path projectRoot, long timeoutMillis) throws IOException {
            throw failure;
        }
    }

    private static final class ErrorExecutor implements TestCommandExecutor {

        @Override
        public TestRun runTests(Path projectRoot, long timeoutMillis) {
            throw new AssertionError("boom");
        }
    }
}
