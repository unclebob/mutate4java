package mutate4java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
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
        deleteIfExists(jacocoDir);
        deleteIfExists(jacocoExec);

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

    private void deleteIfExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        if (Files.isDirectory(path)) {
            try (var walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(current -> {
                            try {
                                Files.deleteIfExists(current);
                            } catch (IOException ex) {
                                throw new IllegalStateException("Failed deleting stale coverage: " + current, ex);
                            }
                        });
            }
            return;
        }
        Files.deleteIfExists(path);
    }
}
