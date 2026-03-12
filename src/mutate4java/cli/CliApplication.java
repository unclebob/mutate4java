package mutate4java.cli;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.engine.*;
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
import java.util.Set;

public final class CliApplication {

    private final ProjectLayout layout;
    private final CliExecution execution;
    private final CliRequestParser requestParser;

    public CliApplication(Path workspaceRoot, PrintStream out, PrintStream err, TestCommandExecutor executor) {
        this(workspaceRoot,
                out,
                err,
                executor,
                new CoverageRunner(new ProcessCommandExecutor()),
                new CopiedWorkspaceManager(),
                new PrintStreamProgressReporter(out));
    }

    public CliApplication(Path workspaceRoot,
                          PrintStream out,
                          PrintStream err,
                          TestCommandExecutor executor,
                          CoverageRunner coverageRunner) {
        this(workspaceRoot,
                out,
                err,
                executor,
                coverageRunner,
                new CopiedWorkspaceManager(),
                new PrintStreamProgressReporter(out));
    }

    public CliApplication(Path workspaceRoot,
                          PrintStream out,
                          PrintStream err,
                          TestCommandExecutor executor,
                          CoverageRunner coverageRunner,
                          WorkspaceManager workspaceManager) {
        this(workspaceRoot, out, err, executor, coverageRunner, workspaceManager, new PrintStreamProgressReporter(out));
    }

    public CliApplication(Path workspaceRoot,
                          PrintStream out,
                          PrintStream err,
                          TestCommandExecutor executor,
                          CoverageRunner coverageRunner,
                          WorkspaceManager workspaceManager,
                          ProgressReporter verboseProgressReporter) {
        this.layout = new ProjectLayout(workspaceRoot);
        this.requestParser = new CliRequestParser(out, err);
        this.execution = new CliExecutionFactory().create(
                workspaceRoot, out, err, executor, coverageRunner, workspaceManager, verboseProgressReporter, layout);
    }

    public int execute(String[] args) throws Exception {
        ParseOutcome parse = requestParser.parse(args);
        if (parse.exitCode() >= 0) {
            return parse.exitCode();
        }
        return execution.execute(parse.arguments());
    }

    public String sourceSuffix(Path moduleRoot, Path file) {
        return layout.sourceSuffix(moduleRoot, file);
    }

    public Path moduleRootFor(List<Path> files) {
        return layout.moduleRootFor(files);
    }
}
