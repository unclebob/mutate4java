package mutate4java;

import java.io.PrintStream;

final class PrintStreamProgressReporter implements ProgressReporter {

    private final PrintStream out;

    PrintStreamProgressReporter(PrintStream out) {
        this.out = out;
    }

    @Override
    public synchronized void runStarting(int totalMutations, int workerCount) {
        out.printf("Running %d mutations with %d workers.%n", totalMutations, workerCount);
    }

    @Override
    public synchronized void mutationStarting(int workerIndex, MutationJob job) {
        out.printf("Worker %d starting %d/%d: %s:%d %s%n",
                workerIndex,
                job.order() + 1,
                job.totalJobs(),
                job.site().file(),
                job.site().lineNumber(),
                job.site().description());
    }

    @Override
    public synchronized void mutationFinished(int workerIndex, MutationResult result) {
        out.printf("Worker %d finished %d/%d: %s %s:%d%n",
                workerIndex,
                result.order() + 1,
                result.totalJobs(),
                result.killed() ? "KILLED" : "SURVIVED",
                result.site().file(),
                result.site().lineNumber());
    }
}
