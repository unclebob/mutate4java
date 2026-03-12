package mutate4java.engine;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.analysis.*;
import mutate4java.cli.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;
import mutate4java.selection.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class MutationExecution {

    private final WorkspaceManager workspaceManager;

    MutationExecution(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

    List<MutationResult> run(Path moduleRoot,
                             List<MutationSite> sites,
                             long timeoutMillis,
                             int maxWorkers,
                             ProgressReporter progressReporter,
                             TestCommandExecutor testExecutor) throws Exception {
        List<MutationJob> jobs = new ArrayList<>();
        for (int i = 0; i < sites.size(); i++) {
            MutationSite site = sites.get(i);
            jobs.add(new MutationJob(site, moduleRoot.relativize(site.file()), timeoutMillis, i, sites.size()));
        }
        int workerCount = Math.max(1, Math.min(jobs.size(), maxWorkers));
        try (WorkerWorkspaces workspaces = workspaceManager.createWorkerWorkspaces(moduleRoot, workerCount);
             WorkerPool pool = new ParallelWorkerPool(workspaces.workerRoots(), testExecutor, progressReporter)) {
            return pool.runAll(jobs);
        }
    }

    long timeoutMillis(long baselineDurationMillis, int timeoutFactor) {
        long baseline = Math.max(1L, baselineDurationMillis);
        return Math.max(1_000L, baseline * timeoutFactor);
    }
}
