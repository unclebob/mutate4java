package mutate4java;

final class NoOpProgressReporter implements ProgressReporter {

    @Override
    public void baselineStarting(java.nio.file.Path moduleRoot) {
    }

    @Override
    public void baselineFinished(TestRun baseline) {
    }

    @Override
    public void runStarting(int totalMutations, int workerCount) {
    }

    @Override
    public void mutationStarting(int workerIndex, MutationJob job) {
    }

    @Override
    public void mutationFinished(int workerIndex, MutationResult result) {
    }
}
