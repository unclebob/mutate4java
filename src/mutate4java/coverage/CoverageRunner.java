package mutate4java.coverage;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.exec.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class CoverageRunner {

    static final long COVERAGE_TIMEOUT_MILLIS = 300_000L;

    private final ProcessCommandExecutor executor;
    private final CoverageCleaner cleaner = new CoverageCleaner();
    public CoverageRunner(ProcessCommandExecutor executor) {
        this.executor = executor;
    }
    public CoverageRun generateCoverage(Path projectRoot) throws Exception {
        Path jacocoDir = projectRoot.resolve("target/site/jacoco");
        Path jacocoExec = projectRoot.resolve("target/jacoco.exec");
        cleaner.deleteStaleCoverage(jacocoDir, jacocoExec);

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
}
