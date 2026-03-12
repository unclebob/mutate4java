package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.Path;

public interface WorkspaceManager {

    WorkerWorkspaces createWorkerWorkspaces(Path moduleRoot, int workerCount) throws IOException;
}
