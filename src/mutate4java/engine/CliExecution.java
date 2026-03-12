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

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

public final class CliExecution {

    private final Path workspaceRoot;
    private final PrintStream out;
    private final TestCommandExecutor testExecutor;
    private final ProgressReporter verboseProgressReporter;
    private final MutationCatalog catalog;
    private final ReportFormatter formatter;
    private final ProjectLayout layout;
    private final BaselineRunner baselineRunner;
    private final MutationRunPlanner mutationRunPlanner;
    private final ScanMode scanMode;
    private final ExecutionOutcomeWriter outcomeWriter;
    private final ManifestWriter manifestWriter;

    public CliExecution(Path workspaceRoot,
                        PrintStream out,
                        PrintStream err,
                        TestCommandExecutor testExecutor,
                        CoverageRunner coverageRunner,
                        ProgressReporter verboseProgressReporter,
                        MutationCatalog catalog,
                        ReportFormatter formatter,
                        ManifestSupport manifestSupport,
                        ProjectLayout layout,
                        DifferentialSelector selector,
                        MutationCoverageFilter coverageFilter,
                        MutationExecution mutationExecution,
                        ScanReportFormatter scanReportFormatter) {
        this.workspaceRoot = workspaceRoot;
        this.out = out;
        this.testExecutor = testExecutor;
        this.verboseProgressReporter = verboseProgressReporter;
        this.catalog = catalog;
        this.formatter = formatter;
        this.layout = layout;
        this.baselineRunner = new BaselineRunner(coverageRunner, err);
        LineFilter lineFilter = new LineFilter();
        this.mutationRunPlanner = new MutationRunPlanner(selector, coverageFilter, mutationExecution,
                new ExecutionMessages(), lineFilter);
        this.scanMode = new ScanMode(selector, scanReportFormatter, lineFilter);
        this.manifestWriter = new ManifestWriter(manifestSupport);
        this.outcomeWriter = new ExecutionOutcomeWriter(workspaceRoot, out, formatter, manifestWriter);
    }

    public int execute(CliArguments parsed) throws Exception {
        ExecutionContext context = ExecutionContext.create(parsed, testExecutor, verboseProgressReporter, layout, catalog);
        if (parsed.scan()) {
            out.print(scanMode.render(parsed, context));
            return 0;
        }
        if (parsed.updateManifest()) {
            manifestWriter.write(context.sourceFile(), context.analysis());
            out.printf("Updated manifest for %s%n", workspaceRoot.relativize(context.sourceFile()));
            return 0;
        }

        CoverageRun coverageRun = baselineRunner.run(parsed, context.executor(), context.moduleRoot(), context.progressReporter());
        TestRun baseline = coverageRun.baseline();
        if (!baseline.passed()) {
            return baselineRunner.fail(baseline);
        }

        MutantResultSummary summary = mutationRunPlanner.run(parsed, context, baseline, coverageRun.report());
        return outcomeWriter.write(summary, context.analysis());
    }
}
