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

import java.nio.file.Path;
import java.util.List;

public record ExecutionContext(Path sourceFile,
                        Path moduleRoot,
                        TestCommandExecutor executor,
                        ProgressReporter progressReporter,
                        SourceAnalysis analysis) {

    public static ExecutionContext create(CliArguments parsed,
                                   TestCommandExecutor executor,
                                   ProgressReporter verboseProgressReporter,
                                   ProjectLayout layout,
                                   MutationCatalog catalog) throws Exception {
        TestCommandExecutor selectedExecutor = parsed.testCommand() == null ? executor : executor.withCommand(parsed.testCommand());
        List<Path> files = List.of(layout.explicitFile(parsed.fileArgs().get(0)));
        Path sourceFile = files.get(0);
        return new ExecutionContext(
                sourceFile,
                layout.moduleRootFor(files),
                selectedExecutor,
                parsed.verbose() ? verboseProgressReporter : new NoOpProgressReporter(),
                catalog.analyze(sourceFile)
        );
    }
}
