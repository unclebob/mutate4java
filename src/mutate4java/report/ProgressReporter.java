package mutate4java.report;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.nio.file.Path;

public interface ProgressReporter {

    void baselineStarting(Path moduleRoot);

    void baselineFinished(TestRun baseline);

    void runStarting(int totalMutations, int workerCount);

    void mutationStarting(int workerIndex, MutationJob job);

    void mutationFinished(int workerIndex, MutationResult result);
}
