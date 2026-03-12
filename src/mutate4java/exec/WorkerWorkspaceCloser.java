package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class WorkerWorkspaceCloser {

    void close(Path runRoot) throws IOException {
        if (!Files.exists(runRoot)) {
            return;
        }
        IOException failure = WorkerCleanup.deleteWithRetries(runRoot, WorkerWorkspaces::tryDelete, WorkerCleanup::sleepBeforeRetry);
        if (failure != null) {
            throw new IllegalStateException("Failed deleting worker workspace: " + runRoot, failure);
        }
    }
}
