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

public final class CliExecutionFactory {

    public CliExecution create(Path workspaceRoot,
                               PrintStream out,
                               PrintStream err,
                               TestCommandExecutor executor,
                               CoverageRunner coverageRunner,
                               WorkspaceManager workspaceManager,
                               ProgressReporter verboseProgressReporter,
                               ProjectLayout layout) {
        ManifestSupport manifestSupport = new ManifestSupport();
        return new CliExecution(
                workspaceRoot,
                out,
                err,
                executor,
                coverageRunner,
                verboseProgressReporter,
                new MutationCatalog(),
                new ReportFormatter(),
                manifestSupport,
                layout,
                new DifferentialSelector(manifestSupport),
                new MutationCoverageFilter(layout),
                new MutationExecution(workspaceManager),
                new ScanReportFormatter(workspaceRoot)
        );
    }
}
