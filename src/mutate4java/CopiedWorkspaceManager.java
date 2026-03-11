package mutate4java;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class CopiedWorkspaceManager implements WorkspaceManager {

    @Override
    public WorkerWorkspaces createWorkerWorkspaces(Path moduleRoot, int workerCount) throws IOException {
        Path workersBase = moduleRoot.resolve("target/mutation-workers");
        Files.createDirectories(workersBase);
        Path runRoot = workersBase.resolve("run-" + UUID.randomUUID());
        Files.createDirectories(runRoot);

        List<Path> workerRoots = new ArrayList<>();
        for (int worker = 1; worker <= workerCount; worker++) {
            Path workerRoot = runRoot.resolve("worker-" + worker);
            copyModule(moduleRoot, workerRoot);
            workerRoots.add(workerRoot);
        }
        return new WorkerWorkspaces(runRoot, List.copyOf(workerRoots));
    }

    private void copyModule(Path moduleRoot, Path workerRoot) throws IOException {
        Path excludedRoot = moduleRoot.resolve("target").normalize();
        Files.walkFileTree(moduleRoot, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.normalize().startsWith(excludedRoot)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Path relative = moduleRoot.relativize(dir);
                Files.createDirectories(workerRoot.resolve(relative));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = moduleRoot.relativize(file);
                Files.copy(file, workerRoot.resolve(relative));
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
