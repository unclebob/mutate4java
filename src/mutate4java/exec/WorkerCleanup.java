package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Path;

final class WorkerCleanup {

    static final int DELETE_RETRIES = 5;
    static final long RETRY_DELAY_MILLIS = 50L;

    private WorkerCleanup() {
    }

    static IOException deleteWithRetries(Path runRoot, WorkerWorkspaces.DeleteAttempt deleteAttempt, WorkerWorkspaces.RetrySleeper retrySleeper) {
        IOException failure = null;
        for (int attempt = 1; attempt <= DELETE_RETRIES; attempt++) {
            failure = deleteAttempt.tryDelete(runRoot);
            if (failure == null) {
                return null;
            }
            if (!(failure instanceof DirectoryNotEmptyException)) {
                throw new IllegalStateException("Failed deleting worker workspace: " + runRoot, failure);
            }
            retrySleeper.sleep();
        }
        return failure;
    }

    static void sleepBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MILLIS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while deleting worker workspace", ex);
        }
    }
}
