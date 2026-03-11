package mutate4java;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class CliApplication {

    private final Path workspaceRoot;
    private final PrintStream out;
    private final PrintStream err;
    private final TestCommandExecutor executor;
    private final CoverageRunner coverageRunner;
    private final WorkspaceManager workspaceManager;
    private final ProgressReporter verboseProgressReporter;
    private final MutationCatalog catalog = new MutationCatalog();
    private final ReportFormatter formatter = new ReportFormatter();

    CliApplication(Path workspaceRoot, PrintStream out, PrintStream err, TestCommandExecutor executor) {
        this(workspaceRoot,
                out,
                err,
                executor,
                new CoverageRunner(new ProcessCommandExecutor()),
                new CopiedWorkspaceManager(),
                new PrintStreamProgressReporter(out));
    }

    CliApplication(Path workspaceRoot,
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

    CliApplication(Path workspaceRoot,
                   PrintStream out,
                   PrintStream err,
                   TestCommandExecutor executor,
                   CoverageRunner coverageRunner,
                   WorkspaceManager workspaceManager) {
        this(workspaceRoot, out, err, executor, coverageRunner, workspaceManager, new PrintStreamProgressReporter(out));
    }

    CliApplication(Path workspaceRoot,
                   PrintStream out,
                   PrintStream err,
                   TestCommandExecutor executor,
                   CoverageRunner coverageRunner,
                   WorkspaceManager workspaceManager,
                   ProgressReporter verboseProgressReporter) {
        this.workspaceRoot = workspaceRoot;
        this.out = out;
        this.err = err;
        this.executor = executor;
        this.coverageRunner = coverageRunner;
        this.workspaceManager = workspaceManager;
        this.verboseProgressReporter = verboseProgressReporter;
    }

    int execute(String[] args) throws Exception {
        ParseOutcome parse = parseArguments(args);
        if (parse.exitCode >= 0) {
            return parse.exitCode;
        }
        CliArguments parsed = parse.arguments;

        List<Path> files = filesForMode(parsed);
        if (files.isEmpty()) {
            out.println("No Java files to mutate.");
            return 0;
        }
        Path moduleRoot = moduleRootFor(files);

        CoverageRun coverageRun = coverageRunner.generateCoverage(moduleRoot);
        TestRun baseline = coverageRun.baseline();
        if (!baseline.passed()) {
            if (baseline.timedOut()) {
                err.println("Baseline tests timed out.");
            }
            err.println("Baseline tests failed.");
            err.print(baseline.output());
            return 2;
        }

        CoverageReport coverage = coverageRun.report();
        List<MutationSite> discovered = filterByLines(catalog.discover(files), parsed.lines());
        CoverageSelection selection = filterCoveredSites(moduleRoot, discovered, coverage);
        if (selection.covered().isEmpty()) {
            out.print(formatter.format(workspaceRoot, baseline, selection.uncovered(), List.of()));
            return 0;
        }

        long timeoutMillis = mutantTimeoutMillis(baseline.durationMillis(), parsed.timeoutFactor());
        List<MutationResult> results = runMutations(
                moduleRoot,
                selection.covered(),
                timeoutMillis,
                parsed.maxWorkers(),
                parsed.verbose()
        );
        out.print(formatter.format(workspaceRoot, baseline, selection.uncovered(), results));
        return results.stream().anyMatch(result -> !result.killed()) ? 3 : 0;
    }

    private ParseOutcome parseArguments(String[] args) {
        try {
            CliArguments parsed = CliArgumentsParser.parse(args);
            if (parsed.mode() == CliMode.HELP) {
                out.println(Main.usage());
                return ParseOutcome.exit(0);
            }
            return ParseOutcome.ok(parsed);
        } catch (IllegalArgumentException ex) {
            err.println(ex.getMessage());
            out.println(Main.usage());
            return ParseOutcome.exit(1);
        }
    }

    private List<Path> filesForMode(CliArguments parsed) throws Exception {
        return switch (parsed.mode()) {
            case EXPLICIT_FILES -> List.of(explicitFile(parsed.fileArgs().get(0)));
            case HELP -> List.of();
        };
    }

    private List<MutationResult> runMutations(Path moduleRoot,
                                              List<MutationSite> sites,
                                              long timeoutMillis,
                                              int maxWorkers,
                                              boolean verbose) throws Exception {
        List<MutationJob> jobs = new ArrayList<>();
        for (int i = 0; i < sites.size(); i++) {
            MutationSite site = sites.get(i);
            jobs.add(new MutationJob(site, moduleRoot.relativize(site.file()), timeoutMillis, i, sites.size()));
        }
        int workerCount = Math.max(1, Math.min(jobs.size(), maxWorkers));
        ProgressReporter progressReporter = verbose ? verboseProgressReporter : new NoOpProgressReporter();
        try (WorkerWorkspaces workspaces = workspaceManager.createWorkerWorkspaces(moduleRoot, workerCount);
             WorkerPool pool = new ParallelWorkerPool(workspaces.workerRoots(), executor, progressReporter)) {
            return pool.runAll(jobs);
        }
    }

    private List<MutationSite> filterByLines(List<MutationSite> sites, Set<Integer> lines) {
        if (lines.isEmpty()) {
            return sites;
        }
        return sites.stream()
                .filter(site -> lines.contains(site.lineNumber()))
                .toList();
    }

    private long mutantTimeoutMillis(long baselineDurationMillis, int timeoutFactor) {
        long baseline = Math.max(1L, baselineDurationMillis);
        return Math.max(1_000L, baseline * timeoutFactor);
    }

    private CoverageSelection filterCoveredSites(Path moduleRoot, List<MutationSite> sites, CoverageReport coverage) {
        List<MutationSite> covered = new ArrayList<>();
        List<MutationSite> uncovered = new ArrayList<>();
        for (MutationSite site : sites) {
            if (coverage.covers(sourceSuffix(moduleRoot, site.file()), site.lineNumber())) {
                covered.add(site);
            } else {
                uncovered.add(site);
            }
        }
        return new CoverageSelection(List.copyOf(covered), List.copyOf(uncovered));
    }

    private String sourceSuffix(Path moduleRoot, Path file) {
        String relative = moduleRoot.relativize(file).toString().replace('\\', '/');
        String prefix = "src/";
        int separator = relative.indexOf('/');
        if (relative.startsWith(prefix) && separator >= 0) {
            return relative.substring(separator + 1);
        }
        return relative;
    }

    private Path explicitFile(String arg) {
        Path path = workspaceRoot.resolve(arg).normalize();
        if (java.nio.file.Files.isDirectory(path)) {
            throw new IllegalArgumentException("mutate4java target must be a .java file");
        }
        return path;
    }

    private Path moduleRootFor(List<Path> files) {
        Path moduleRoot = null;
        for (Path file : files) {
            Path current = findModuleRoot(file);
            if (current == null) {
                current = workspaceRoot;
            }
            if (moduleRoot == null) {
                moduleRoot = current;
            } else if (!moduleRoot.equals(current)) {
                throw new IllegalArgumentException("All targets must be in the same Maven module");
            }
        }
        return moduleRoot == null ? workspaceRoot : moduleRoot;
    }

    private Path findModuleRoot(Path file) {
        Path current = java.nio.file.Files.isDirectory(file) ? file : file.getParent();
        while (current != null && current.startsWith(workspaceRoot)) {
            if (java.nio.file.Files.exists(current.resolve("pom.xml"))) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    private record ParseOutcome(CliArguments arguments, int exitCode) {

        private static ParseOutcome ok(CliArguments arguments) {
            return new ParseOutcome(arguments, -1);
        }

        private static ParseOutcome exit(int code) {
            return new ParseOutcome(null, code);
        }
    }

    private record CoverageSelection(List<MutationSite> covered, List<MutationSite> uncovered) {
    }
}
