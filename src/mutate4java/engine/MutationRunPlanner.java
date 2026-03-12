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

import java.util.List;

final class MutationRunPlanner {

    private final DifferentialSelector selector;
    private final MutationCoverageFilter coverageFilter;
    private final MutationExecution mutationExecution;
    private final ExecutionMessages messages;
    private final LineFilter lineFilter;

    MutationRunPlanner(DifferentialSelector selector,
                       MutationCoverageFilter coverageFilter,
                       MutationExecution mutationExecution,
                       ExecutionMessages messages,
                       LineFilter lineFilter) {
        this.selector = selector;
        this.coverageFilter = coverageFilter;
        this.mutationExecution = mutationExecution;
        this.messages = messages;
        this.lineFilter = lineFilter;
    }

    MutantResultSummary run(CliArguments parsed, ExecutionContext context, TestRun baseline, CoverageReport coverage) throws Exception {
        DifferentialSelection differentialSelection = selector.select(context.sourceFile(), parsed, context.analysis());
        List<MutationSite> discovered = lineFilter.filter(differentialSelection.selected(), parsed.lines());
        CoverageSelection coverageSelection = coverageFilter.filter(context.moduleRoot(), discovered, coverage);
        String extra = messages.extraText(parsed, differentialSelection, coverageSelection);
        if (coverageSelection.covered().isEmpty()) {
            return new MutantResultSummary(context.sourceFile(), baseline, extra, coverageSelection.uncovered(), List.of());
        }

        long timeoutMillis = mutationExecution.timeoutMillis(baseline.durationMillis(), parsed.timeoutFactor());
        List<MutationResult> results = mutationExecution.run(context.moduleRoot(), coverageSelection.covered(),
                timeoutMillis, parsed.maxWorkers(), context.progressReporter(), context.executor());
        return new MutantResultSummary(context.sourceFile(), baseline, extra, coverageSelection.uncovered(), results);
    }
}
