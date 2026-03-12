package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Path;
import java.util.List;

public record WorkerWorkspaces(Path runRoot, List<Path> workerRoots) implements AutoCloseable {

    @Override
    public void close() throws IOException {
        new WorkerWorkspaceCloser().close(runRoot);
    }
    public static IOException tryDelete(Path runRoot) {
        return tryDelete(runRoot, new WorkerDirectoryDelete()::delete);
    }

    public static IOException tryDelete(Path runRoot, DeleteTree deleteTree) {
        try {
            deleteTree.delete(runRoot);
            return null;
        } catch (IOException ex) {
            return ex;
        }
    }
    public static IOException deleteWithRetries(Path runRoot, DeleteAttempt deleteAttempt, RetrySleeper retrySleeper) {
        return WorkerCleanup.deleteWithRetries(runRoot, deleteAttempt::tryDelete, retrySleeper::sleep);
    }

    @FunctionalInterface
    public interface DeleteAttempt {
        IOException tryDelete(Path runRoot);
    }

    @FunctionalInterface
    public interface RetrySleeper {
        void sleep();
    }

    @FunctionalInterface
    public interface DeleteTree {
        void delete(Path runRoot) throws IOException;
    }
}
