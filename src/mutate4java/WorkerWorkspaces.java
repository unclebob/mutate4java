package mutate4java;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.List;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;

record WorkerWorkspaces(Path runRoot, List<Path> workerRoots) implements AutoCloseable {

    private static final int DELETE_RETRIES = 5;
    private static final long RETRY_DELAY_MILLIS = 50L;

    @Override
    public void close() throws IOException {
        if (!Files.exists(runRoot)) {
            return;
        }
        IOException failure = deleteWithRetries(runRoot, WorkerWorkspaces::tryDelete, WorkerWorkspaces::sleepBeforeRetry);
        if (failure != null) {
            throw new IllegalStateException("Failed deleting worker workspace: " + runRoot, failure);
        }
    }

    static IOException deleteWithRetries(Path runRoot, DeleteAttempt deleteAttempt, RetrySleeper retrySleeper) {
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

    static IOException tryDelete(Path runRoot) {
        return tryDelete(runRoot, WorkerWorkspaces::deleteTree);
    }

    static IOException tryDelete(Path runRoot, DeleteTree deleteTree) {
        try {
            deleteTree.delete(runRoot);
            return null;
        } catch (IOException ex) {
            return ex;
        }
    }

    private static void deleteTree(Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void sleepBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MILLIS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while deleting worker workspace", ex);
        }
    }

    @FunctionalInterface
    interface DeleteAttempt {
        IOException tryDelete(Path runRoot);
    }

    @FunctionalInterface
    interface RetrySleeper {
        void sleep();
    }

    @FunctionalInterface
    interface DeleteTree {
        void delete(Path runRoot) throws IOException;
    }
}
