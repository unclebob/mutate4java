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
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkerWorkspacesTest {

    @TempDir
    Path tempDir;

    @Test
    void closeDoesNothingWhenRunRootDoesNotExist() {
        WorkerWorkspaces workspaces = new WorkerWorkspaces(tempDir.resolve("missing"), List.of());

        assertDoesNotThrow(workspaces::close);
    }

    @Test
    void closeDeletesRunRootTree() throws Exception {
        Path runRoot = tempDir.resolve("run");
        Path nestedFile = runRoot.resolve("worker-1/target/surefire-reports/result.txt");
        Files.createDirectories(nestedFile.getParent());
        Files.writeString(nestedFile, "done");
        WorkerWorkspaces workspaces = new WorkerWorkspaces(runRoot, List.of(runRoot.resolve("worker-1")));

        workspaces.close();

        assertFalse(Files.exists(runRoot));
    }

    @Test
    void deleteWithRetriesThrowsForNonRetryableFailure() {
        IOException failure = new IOException("boom");

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> WorkerWorkspaces.deleteWithRetries(tempDir, path -> failure, () -> {
                }));

        assertSame(failure, thrown.getCause());
    }

    @Test
    void deleteWithRetriesReturnsLastDirectoryNotEmptyFailureAfterRetryLimit() {
        DirectoryNotEmptyException failure = new DirectoryNotEmptyException("busy");
        AtomicInteger sleeps = new AtomicInteger();

        IOException result = WorkerWorkspaces.deleteWithRetries(tempDir, path -> failure, sleeps::incrementAndGet);

        assertSame(failure, result);
        assertSame(5, sleeps.get());
    }

    @Test
    void tryDeleteReturnsIOExceptionFromDeleteTree() throws Exception {
        IOException failure = new IOException("boom");

        IOException result = WorkerWorkspaces.tryDelete(tempDir, path -> {
            throw failure;
        });

        assertSame(failure, result);
    }
}
