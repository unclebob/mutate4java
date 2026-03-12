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

final class ProgressMessageFormatter {

    String baselineStarting(Path moduleRoot) {
        return "Baseline starting for %s%n".formatted(moduleRoot);
    }

    String baselineFinished(TestRun baseline) {
        return "Baseline finished: exit=%d timedOut=%s duration=%d ms%n"
                .formatted(baseline.exitCode(), baseline.timedOut(), baseline.durationMillis());
    }

    String runStarting(int totalMutations, int workerCount) {
        return "Running %d mutations with %d workers.%n".formatted(totalMutations, workerCount);
    }

    String mutationStarting(int workerIndex, MutationJob job) {
        return "Worker %d starting %d/%d: %s:%d %s%n".formatted(
                workerIndex, job.order() + 1, job.totalJobs(), job.site().file(), job.site().lineNumber(), job.site().description());
    }

    String mutationFinished(int workerIndex, MutationResult result) {
        return "Worker %d finished %d/%d: %s %s:%d%n".formatted(
                workerIndex, result.order() + 1, result.totalJobs(),
                result.killed() ? "KILLED" : "SURVIVED", result.site().file(), result.site().lineNumber());
    }
}
