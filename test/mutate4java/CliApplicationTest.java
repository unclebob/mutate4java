package mutate4java;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliApplicationTest {

    private final ManifestSupport manifestSupport = new ManifestSupport();

    @TempDir
    Path tempDir;

    @Test
    void reportsMutationProgress() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(1, "killed", 5, false)
        );
        ByteArrayOutputStream progress = new ByteArrayOutputStream();

        int exit = new CliApplication(
                tempDir,
                new PrintStream(new ByteArrayOutputStream()),
                new PrintStream(new ByteArrayOutputStream()),
                executor,
                coverageRunner,
                new CopiedWorkspaceManager(),
                new PrintStreamProgressReporter(new PrintStream(progress))
        ).execute(new String[]{relative(file), "--verbose"});

        assertEquals(0, exit);
        assertTrue(progress.toString().contains("Baseline starting for"));
        assertTrue(progress.toString().contains("Baseline finished: exit=0 timedOut=false duration=10 ms"));
        assertTrue(progress.toString().contains("Running 1 mutations with 1 workers."));
        assertTrue(progress.toString().contains("Worker 1 starting 1/1:"));
        assertTrue(progress.toString().contains("Worker 1 finished 1/1: KILLED"));
    }

    @Test
    void printsHelpAndExitsZero() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int exit = application(out, err, new StubExecutor(), new StubCoverageRunner(new CoverageReport(Set.of())))
                .execute(new String[]{"--help"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("Usage:"));
    }

    @Test
    void scansMutationSitesWithoutRunningCoverageOrMutants() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of()));
        StubExecutor executor = new StubExecutor();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file), "--scan"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("Scan: 2 mutation sites in src/main/java/demo/Sample.java"));
        assertTrue(out.toString().contains("src/main/java/demo/Sample.java:5 replace true with false"));
        assertTrue(out.toString().contains("src/main/java/demo/Sample.java:9 replace == with !="));
        assertEquals(0, executor.invocations.get());
        assertEquals(0, coverageRunner.invocations.get());
        assertEquals(originalSource(), strippedSource(file));
    }

    @Test
    void updatesManifestWithoutRunningCoverageOrMutants() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of()));
        StubExecutor executor = new StubExecutor();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file), "--update-manifest"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("Updated manifest for src/main/java/demo/Sample.java"));
        assertEquals(0, executor.invocations.get());
        assertEquals(0, coverageRunner.invocations.get());
        assertEquals(originalSource(), strippedSource(file));
        assertTrue(manifestSupport.read(file).isPresent());
    }

    @Test
    void marksChangedScopesDuringScanWhenManifestDiffers() throws Exception {
        Path file = writeSourceFile();
        SourceAnalysis manifestBaseline = new MutationCatalog().analyze(file);
        writeMatchingManifest(file);
        String changedSource = """
                package demo;

                class Sample {
                    boolean truthy() {
                        return false;
                    }

                    boolean same(int left, int right) {
                        return left == right;
                    }
                }
                """;
        manifestSupport.write(file,
                changedSource,
                new DifferentialManifest(1, manifestBaseline.moduleHash(), manifestBaseline.scopes()));
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), new StubExecutor(), coverageRunner)
                .execute(new String[]{relative(file), "--scan"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("* src/main/java/demo/Sample.java:5 replace false with true"));
        assertTrue(out.toString().contains("  src/main/java/demo/Sample.java:9 replace == with !="));
        assertTrue(out.toString().contains("* indicates a scope that differs from the embedded manifest."));
        assertEquals(0, coverageRunner.invocations.get());
    }

    @Test
    void printsUsageForInvalidArgumentsAndExitsOne() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int exit = application(out, err, new StubExecutor(), new StubCoverageRunner(new CoverageReport(Set.of())))
                .execute(new String[]{"bogus"});

        assertEquals(1, exit);
        assertTrue(out.toString().contains("Usage:"));
        assertTrue(err.toString().contains("mutate4java target must be a .java file"));
    }

    @Test
    void stopsWhenBaselineTestsFail() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(
                new TestRun(1, "failing baseline", 10, false),
                new CoverageReport(Set.of())
        );
        StubExecutor executor = new StubExecutor();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int exit = application(out, err, executor, coverageRunner)
                .execute(new String[]{relative(file)});

        assertEquals(2, exit);
        assertTrue(err.toString().contains("Baseline tests failed."));
        assertEquals(originalSource(), strippedSource(file));
        assertEquals(0, executor.invocations.get());
    }

    @Test
    void returnsNonZeroWhenAnyMutationSurvives() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5),
                new CoverageSite("demo/Sample.java", 9)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(1, "killed", 5, false),
                new TestRun(0, "survived", 6, false)
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file)});

        assertEquals(3, exit);
        assertTrue(out.toString().contains("KILLED"));
        assertTrue(out.toString().contains("SURVIVED"));
        assertEquals(originalSource(), strippedSource(file));
    }

    @Test
    void returnsZeroWhenAllMutationsAreKilled() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5),
                new CoverageSite("demo/Sample.java", 9)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(1, "killed", 5, false),
                new TestRun(1, "killed", 6, false)
        );

        int exit = application(new ByteArrayOutputStream(), new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file)});

        assertEquals(0, exit);
        assertEquals(originalSource(), strippedSource(file));
    }

    @Test
    void returnsZeroWhenAllDiscoveredSitesAreUncovered() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of()));
        StubExecutor executor = new StubExecutor();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file)});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("Coverage: 2 uncovered sites skipped."));
        assertEquals(0, executor.invocations.get());
    }

    @Test
    void filtersMutationsByRequestedLines() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(1, "killed", 5, false)
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file), "--lines", "5"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("Summary: 1 killed, 0 survived, 1 total."));
        assertEquals(1, executor.invocations.get());
        assertEquals(1000L, executor.timeouts.remove());
    }

    @Test
    void countsTimedOutMutantsAsKilled() throws Exception {
        Path file = writeUnarySourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Guard.java", 5)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(124, "timed out mutant", 1000, true)
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file), "--lines", "5"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("timed out"));
        assertEquals(unarySource(), strippedSource(file));
    }

    @Test
    void reportsUncoveredSitesAndSkipsThem() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(1, "killed", 5, false)
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file)});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("UNCOVERED src/main/java/demo/Sample.java:9 replace == with !="));
        assertTrue(out.toString().contains("Coverage: 1 uncovered sites skipped."));
        assertEquals(1, executor.invocations.get());
    }

    @Test
    void acceptsMaxWorkersDuringMutationRun() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5),
                new CoverageSite("demo/Sample.java", 9)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(1, "killed", 5, false),
                new TestRun(1, "killed", 6, false)
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file), "--max-workers", "2"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("Summary: 2 killed, 0 survived, 2 total."));
        assertEquals(2, executor.invocations.get());
        assertEquals(originalSource(), strippedSource(file));
    }

    @Test
    void printsWarningWhenSelectedMutationCountExceedsThreshold() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5),
                new CoverageSite("demo/Sample.java", 9)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(1, "killed", 5, false),
                new TestRun(1, "killed", 5, false)
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file), "--mutation-warning", "1"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("WARNING: Found 2 mutations. Consider splitting this module."));
    }

    @Test
    void skipsMutationsWhenManifestMatchesCurrentModuleHash() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5),
                new CoverageSite("demo/Sample.java", 9)
        )));
        StubExecutor executor = new StubExecutor();
        writeMatchingManifest(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file), "--since-last-run"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("No mutations need testing."));
        assertEquals(0, executor.invocations.get());
    }

    @Test
    void mutateAllIgnoresMatchingManifest() throws Exception {
        Path file = writeSourceFile();
        StubCoverageRunner coverageRunner = new StubCoverageRunner(new CoverageReport(Set.of(
                new CoverageSite("demo/Sample.java", 5),
                new CoverageSite("demo/Sample.java", 9)
        )));
        StubExecutor executor = new StubExecutor(
                new TestRun(1, "killed", 5, false),
                new TestRun(1, "killed", 5, false)
        );
        writeMatchingManifest(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, coverageRunner)
                .execute(new String[]{relative(file), "--mutate-all"});

        assertEquals(0, exit);
        assertTrue(!out.toString().contains("No mutations need testing."));
        assertEquals(2, executor.invocations.get());
    }

    @Test
    void usesCustomTestCommandAndTreatsSitesAsCovered() throws Exception {
        Path file = writeSourceFile();
        StubExecutor executor = new StubExecutor(
                new TestRun(0, "baseline ok", 10, false),
                new TestRun(1, "killed", 5, false),
                new TestRun(1, "killed", 6, false)
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = application(out, new ByteArrayOutputStream(), executor, new StubCoverageRunner(new CoverageReport(Set.of())))
                .execute(new String[]{relative(file), "--test-command", "mvn test -DexcludeTags=no-mutate"});

        assertEquals(0, exit);
        assertTrue(out.toString().contains("Summary: 2 killed, 0 survived, 2 total."));
        assertEquals(3, executor.invocations.get());
        assertEquals(List.of(
                "mvn test -DexcludeTags=no-mutate",
                "mvn test -DexcludeTags=no-mutate",
                "mvn test -DexcludeTags=no-mutate"
        ), List.copyOf(executor.commands));
    }

    @Test
    void usesWorkspaceRootWhenNoPomExistsAboveTargetFile() throws Exception {
        Path file = writeSourceFile();

        Path moduleRoot = application(new ByteArrayOutputStream(), new ByteArrayOutputStream(),
                new StubExecutor(), new StubCoverageRunner(new CoverageReport(Set.of())))
                .moduleRootFor(List.of(file));

        assertEquals(tempDir, moduleRoot);
    }

    @Test
    void findsNearestPomAsModuleRoot() throws Exception {
        Path moduleRoot = tempDir.resolve("tools/mutate4java");
        Path file = moduleRoot.resolve("src/mutate4java/Sample.java");
        Files.createDirectories(file.getParent());
        Files.writeString(moduleRoot.resolve("pom.xml"), "<project/>");
        Files.writeString(file, originalSource());

        Path resolved = application(new ByteArrayOutputStream(), new ByteArrayOutputStream(),
                new StubExecutor(), new StubCoverageRunner(new CoverageReport(Set.of())))
                .moduleRootFor(List.of(file));

        assertEquals(moduleRoot, resolved);
    }

    @Test
    void keepsRelativeSourcePathWhenFileIsOutsideSrcTree() throws Exception {
        Path file = tempDir.resolve("demo/Sample.java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, originalSource());

        String suffix = application(new ByteArrayOutputStream(), new ByteArrayOutputStream(),
                new StubExecutor(), new StubCoverageRunner(new CoverageReport(Set.of())))
                .sourceSuffix(tempDir, file);

        assertEquals("demo/Sample.java", suffix);
    }

    @Test
    void stripsStandardMavenJavaSourcePrefixFromCoverageLookupPath() throws Exception {
        Path file = writeSourceFile();

        String suffix = application(new ByteArrayOutputStream(), new ByteArrayOutputStream(),
                new StubExecutor(), new StubCoverageRunner(new CoverageReport(Set.of())))
                .sourceSuffix(tempDir, file);

        assertEquals("demo/Sample.java", suffix);
    }

    private Path writeSourceFile() throws Exception {
        Path file = tempDir.resolve("src/main/java/demo/Sample.java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, originalSource());
        return file;
    }

    private Path writeUnarySourceFile() throws Exception {
        Path file = tempDir.resolve("src/main/java/demo/Guard.java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, unarySource());
        return file;
    }

    private String relative(Path file) {
        return tempDir.relativize(file).toString();
    }

    private String strippedSource(Path file) throws Exception {
        return manifestSupport.stripManifest(Files.readString(file));
    }

    private void writeMatchingManifest(Path file) throws Exception {
        SourceAnalysis analysis = new MutationCatalog().analyze(file);
        manifestSupport.write(file,
                analysis.sourceWithoutManifest(),
                new DifferentialManifest(1, analysis.moduleHash(), analysis.scopes()));
    }

    private String originalSource() {
        return """
                package demo;

                class Sample {
                    boolean truthy() {
                        return true;
                    }

                    boolean same(int left, int right) {
                        return left == right;
                    }
                }
                """;
    }

    private String unarySource() {
        return """
                package demo;

                class Guard {
                    boolean allows(boolean blocked) {
                        return !blocked;
                    }
                }
                """;
    }

    private CliApplication application(ByteArrayOutputStream out,
                                       ByteArrayOutputStream err,
                                       StubExecutor executor,
                                       StubCoverageRunner coverageRunner) {
        return new CliApplication(
                tempDir,
                new PrintStream(out),
                new PrintStream(err),
                executor,
                coverageRunner,
                new CopiedWorkspaceManager(),
                new NoOpProgressReporter()
        );
    }

    private static final class StubExecutor implements TestCommandExecutor {
        private final Queue<TestRun> runs;
        private final Queue<Long> timeouts;
        private final Queue<String> commands;
        private final AtomicInteger invocations;
        private final String command;

        private StubExecutor(TestRun... values) {
            this(new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(), new ConcurrentLinkedQueue<>(),
                    new AtomicInteger(), null, values);
        }

        private StubExecutor(Queue<TestRun> runs,
                             Queue<Long> timeouts,
                             Queue<String> commands,
                             AtomicInteger invocations,
                             String command,
                             TestRun... values) {
            this.runs = runs;
            this.timeouts = timeouts;
            this.commands = commands;
            this.invocations = invocations;
            this.command = command;
            for (TestRun value : values) {
                this.runs.add(value);
            }
        }

        @Override
        public TestRun runTests(Path projectRoot, long timeoutMillis) {
            invocations.incrementAndGet();
            timeouts.add(timeoutMillis);
            if (command != null) {
                commands.add(command);
            }
            return runs.remove();
        }

        @Override
        public TestCommandExecutor withCommand(String command) {
            return new StubExecutor(runs, timeouts, commands, invocations, command);
        }
    }

    private static final class StubCoverageRunner extends CoverageRunner {
        private final CoverageRun run;
        private final AtomicInteger invocations = new AtomicInteger();

        private StubCoverageRunner(CoverageReport report) {
            super(new ProcessCommandExecutor());
            this.run = new CoverageRun(new TestRun(0, "baseline ok", 10, false), report);
        }

        private StubCoverageRunner(TestRun baseline, CoverageReport report) {
            super(new ProcessCommandExecutor());
            this.run = new CoverageRun(baseline, report);
        }

        @Override
        public CoverageRun generateCoverage(Path projectRoot) {
            invocations.incrementAndGet();
            return run;
        }
    }

    private static final class NoOpProgressReporter implements ProgressReporter {

        @Override
        public void baselineStarting(Path moduleRoot) {
        }

        @Override
        public void baselineFinished(TestRun baseline) {
        }

        @Override
        public void runStarting(int totalMutations, int workerCount) {
        }

        @Override
        public void mutationStarting(int workerIndex, MutationJob job) {
        }

        @Override
        public void mutationFinished(int workerIndex, MutationResult result) {
        }
    }
}
