package mutate4java;

interface ProgressReporter {

    void runStarting(int totalMutations, int workerCount);

    void mutationStarting(int workerIndex, MutationJob job);

    void mutationFinished(int workerIndex, MutationResult result);
}
