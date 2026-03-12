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

final class ExecutionOutcomeWriter {

    private final Path workspaceRoot;
    private final PrintStream out;
    private final ReportFormatter formatter;
    private final ManifestWriter manifestWriter;

    ExecutionOutcomeWriter(Path workspaceRoot, PrintStream out, ReportFormatter formatter, ManifestWriter manifestWriter) {
        this.workspaceRoot = workspaceRoot;
        this.out = out;
        this.formatter = formatter;
        this.manifestWriter = manifestWriter;
    }

    int write(MutantResultSummary summary, SourceAnalysis analysis) throws Exception {
        if (summary.results().isEmpty()) {
            manifestWriter.write(summary.sourceFile(), analysis);
            out.print(formatter.format(workspaceRoot, summary.baseline(), summary.extra(), summary.uncovered(), List.of()));
            return 0;
        }

        int exit = summary.results().stream().anyMatch(result -> !result.killed()) ? 3 : 0;
        if (exit == 0) {
            manifestWriter.write(summary.sourceFile(), analysis);
        }
        out.print(formatter.format(workspaceRoot, summary.baseline(), summary.extra(), summary.uncovered(), summary.results()));
        return exit;
    }
}
