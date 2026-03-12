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

final class BaselineRunner {

    private final CoverageRunner coverageRunner;
    private final PrintStream err;

    BaselineRunner(CoverageRunner coverageRunner, PrintStream err) {
        this.coverageRunner = coverageRunner;
        this.err = err;
    }

    CoverageRun run(CliArguments parsed,
                    TestCommandExecutor executor,
                    Path moduleRoot,
                    ProgressReporter progressReporter) throws Exception {
        progressReporter.baselineStarting(moduleRoot);
        CoverageRun coverageRun = parsed.testCommand() == null
                ? coverageRunner.generateCoverage(moduleRoot)
                : new CoverageRun(executor.runTests(moduleRoot, 0L), CoverageReport.allCovered());
        progressReporter.baselineFinished(coverageRun.baseline());
        return coverageRun;
    }

    int fail(TestRun baseline) {
        if (baseline.timedOut()) {
            err.println("Baseline tests timed out.");
        }
        err.println("Baseline tests failed.");
        err.print(baseline.output());
        return 2;
    }
}
