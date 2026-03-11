package mutate4java;

import java.nio.file.Path;

interface ProgressReporter {

    void baselineStarting(Path moduleRoot);

    void baselineFinished(TestRun baseline);

    void runStarting(int totalMutations, int workerCount);

    void mutationStarting(int workerIndex, MutationJob job);

    void mutationFinished(int workerIndex, MutationResult result);
}
