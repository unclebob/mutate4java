package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.nio.file.Files;
import java.nio.file.Path;

final class IsolatedMutationWorker implements MutationWorker {

    private final Path workerModuleRoot;
    private final TestCommandExecutor executor;
    private final ProgressReporter progressReporter;
    private final int workerIndex;

    IsolatedMutationWorker(Path workerModuleRoot,
                           TestCommandExecutor executor,
                           ProgressReporter progressReporter,
                           int workerIndex) {
        this.workerModuleRoot = workerModuleRoot;
        this.executor = executor;
        this.progressReporter = progressReporter;
        this.workerIndex = workerIndex;
    }

    @Override
    public MutationResult run(MutationJob job) throws Exception {
        progressReporter.mutationStarting(workerIndex, job);
        Path workerFile = workerModuleRoot.resolve(job.sourceRelativePath());
        String original = Files.readString(workerFile);
        Files.writeString(workerFile, mutatedSource(original, job.site()));
        try {
            TestRun run = executor.runTests(workerModuleRoot, job.timeoutMillis());
            MutationResult result = new MutationResult(
                    job.site(),
                    !run.passed(),
                    run.durationMillis(),
                    run.timedOut(),
                    job.order(),
                    job.totalJobs()
            );
            progressReporter.mutationFinished(workerIndex, result);
            return result;
        } finally {
            Files.writeString(workerFile, original);
        }
    }

    private String mutatedSource(String source, MutationSite site) {
        return source.substring(0, site.start())
                + site.replacementText()
                + source.substring(site.end());
    }
}
