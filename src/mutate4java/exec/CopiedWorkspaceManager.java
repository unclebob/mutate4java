package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CopiedWorkspaceManager implements WorkspaceManager {

    private final ModuleTreeCopier copier = new ModuleTreeCopier();

    @Override
    public WorkerWorkspaces createWorkerWorkspaces(Path moduleRoot, int workerCount) throws IOException {
        Path workersBase = moduleRoot.resolve("target/mutation-workers");
        Files.createDirectories(workersBase);
        Path runRoot = workersBase.resolve("run-" + UUID.randomUUID());
        Files.createDirectories(runRoot);

        List<Path> workerRoots = new ArrayList<>();
        for (int worker = 1; worker <= workerCount; worker++) {
            Path workerRoot = runRoot.resolve("worker-" + worker);
            copier.copy(moduleRoot, workerRoot);
            workerRoots.add(workerRoot);
        }
        return new WorkerWorkspaces(runRoot, List.copyOf(workerRoots));
    }
}
