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
            out.print(formatter.format(workspaceRoot, summary.baseline(), summary.extra(),
                    summary.uncovered(), List.of()));
            return 0;
        }

        int exit = summary.results().stream().anyMatch(result -> !result.killed()) ? 3 : 0;
        if (exit == 0) {
            manifestWriter.write(summary.sourceFile(), analysis);
        }
        out.print(formatter.format(workspaceRoot, summary.baseline(), summary.extra(),
                summary.uncovered(), summary.results()));
        return exit;
    }
}

/* mutate4java-manifest
version=1
moduleHash=ec972de5575a2639d8e3e5bb33c92c4270db650c1366397db572dbf6b459ae20
scope.0.id=Y2xhc3M6RXhlY3V0aW9uT3V0Y29tZVdyaXRlciNFeGVjdXRpb25PdXRjb21lV3JpdGVyOjI3
scope.0.kind=class
scope.0.startLine=27
scope.0.endLine=57
scope.0.semanticHash=78879c1fcf140afa027a1e59b99f366150a7ac2e077d4ec7f2d7ecf50c9bd4a2
scope.1.id=ZmllbGQ6RXhlY3V0aW9uT3V0Y29tZVdyaXRlciNmb3JtYXR0ZXI6MzE
scope.1.kind=field
scope.1.startLine=31
scope.1.endLine=31
scope.1.semanticHash=80b22fb2f02647cdbf8821419bfe71dfb597135cddc85a2cfae2fa3af41dcf27
scope.2.id=ZmllbGQ6RXhlY3V0aW9uT3V0Y29tZVdyaXRlciNtYW5pZmVzdFdyaXRlcjozMg
scope.2.kind=field
scope.2.startLine=32
scope.2.endLine=32
scope.2.semanticHash=5b00c399104953594ae3dd7660010cb73288b3ed5908001920e56eb252cd6466
scope.3.id=ZmllbGQ6RXhlY3V0aW9uT3V0Y29tZVdyaXRlciNvdXQ6MzA
scope.3.kind=field
scope.3.startLine=30
scope.3.endLine=30
scope.3.semanticHash=b98df4fbf291f7cd01ba32f6b30c169fc64c08011a73e48a271561a4fcdd0a52
scope.4.id=ZmllbGQ6RXhlY3V0aW9uT3V0Y29tZVdyaXRlciN3b3Jrc3BhY2VSb290OjI5
scope.4.kind=field
scope.4.startLine=29
scope.4.endLine=29
scope.4.semanticHash=ea8f3cda099696b9fbb3052dac911c417ff5d1db470f4d0f8f5ec10ce687102c
scope.5.id=bWV0aG9kOkV4ZWN1dGlvbk91dGNvbWVXcml0ZXIjY3Rvcig0KTozNA
scope.5.kind=method
scope.5.startLine=34
scope.5.endLine=39
scope.5.semanticHash=9360e39dc4f1a8fe94c44e30eb7c52608cc44674afd5b7f3e7f02b90a2dd7f3b
scope.6.id=bWV0aG9kOkV4ZWN1dGlvbk91dGNvbWVXcml0ZXIjd3JpdGUoMik6NDE
scope.6.kind=method
scope.6.startLine=41
scope.6.endLine=56
scope.6.semanticHash=71ef8fbbec76601ebd7625bdc87fe4de633f0e5458d20e2f1b134dd0d5815abc
*/
