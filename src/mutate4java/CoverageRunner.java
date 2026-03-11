package mutate4java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

class CoverageRunner {

    static final long COVERAGE_TIMEOUT_MILLIS = 300_000L;

    private final ProcessCommandExecutor executor;

    CoverageRunner(ProcessCommandExecutor executor) {
        this.executor = executor;
    }

    CoverageRun generateCoverage(Path projectRoot) throws Exception {
        Path jacocoDir = projectRoot.resolve("target/site/jacoco");
        Path jacocoExec = projectRoot.resolve("target/jacoco.exec");
        deleteStaleCoverage(jacocoDir, jacocoExec);

        CommandResult result = executor.run(List.of(
                "mvn", "-q",
                "org.jacoco:jacoco-maven-plugin:0.8.12:prepare-agent",
                "test",
                "org.jacoco:jacoco-maven-plugin:0.8.12:report"
        ), projectRoot, COVERAGE_TIMEOUT_MILLIS);
        CoverageReport report = result.exitCode() == 0
                ? JacocoLineCoverageParser.parse(jacocoDir.resolve("jacoco.xml"))
                : new CoverageReport(Set.of());
        return new CoverageRun(
                new TestRun(result.exitCode(), result.output(), result.durationMillis(), result.timedOut()),
                report
        );
    }

    private void deleteStaleCoverage(Path jacocoDir, Path jacocoExec) throws IOException {
        deleteTreeIfPresent(jacocoDir);
        deleteFileIfPresent(jacocoExec);
    }

    private void deleteTreeIfPresent(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            return;
        }
        try (var walk = Files.walk(path)) {
            walk.sorted(java.util.Comparator.reverseOrder())
                    .forEach(this::deletePath);
        }
    }

    private void deleteFileIfPresent(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            Files.deleteIfExists(path);
        }
    }

    private void deletePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed deleting stale coverage: " + path, ex);
        }
    }
}
