package mutate4java.report;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

public final class NoOpProgressReporter implements ProgressReporter {

    public NoOpProgressReporter() {
    }

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
