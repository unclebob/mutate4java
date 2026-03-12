package mutate4java.report;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.io.PrintStream;
import java.nio.file.Path;

public final class PrintStreamProgressReporter implements ProgressReporter {

    private final PrintStream out;
    private final ProgressMessageFormatter formatter = new ProgressMessageFormatter();

    public PrintStreamProgressReporter(PrintStream out) {
        this.out = out;
    }

    @Override
    public synchronized void baselineStarting(Path moduleRoot) {
        out.print(formatter.baselineStarting(moduleRoot));
    }

    @Override
    public synchronized void baselineFinished(TestRun baseline) {
        out.print(formatter.baselineFinished(baseline));
    }

    @Override
    public synchronized void runStarting(int totalMutations, int workerCount) {
        out.print(formatter.runStarting(totalMutations, workerCount));
    }

    @Override
    public synchronized void mutationStarting(int workerIndex, MutationJob job) {
        out.print(formatter.mutationStarting(workerIndex, job));
    }

    @Override
    public synchronized void mutationFinished(int workerIndex, MutationResult result) {
        out.print(formatter.mutationFinished(workerIndex, result));
    }
}
