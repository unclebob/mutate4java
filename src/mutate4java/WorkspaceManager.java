package mutate4java;

import java.io.IOException;
import java.nio.file.Path;

interface WorkspaceManager {

    WorkerWorkspaces createWorkerWorkspaces(Path moduleRoot, int workerCount) throws IOException;
}
