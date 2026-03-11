package mutate4java;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoverageRunnerIT {

    @Test
    void deletesStaleCoverageArtifactsBeforeRunningCoverage() throws Exception {
        Path projectRoot = TestProjectFactory.createProject("coverage-runner-stale");
        Path jacocoDir = projectRoot.resolve("target/site/jacoco");
        Path jacocoExec = projectRoot.resolve("target/jacoco.exec");
        Files.createDirectories(jacocoDir);
        Files.writeString(jacocoDir.resolve("old.xml"), "stale");
        Files.createDirectories(jacocoExec.getParent());
        Files.writeString(jacocoExec, "stale");

        CoverageRun run = new CoverageRunner(new ProcessCommandExecutor()).generateCoverage(projectRoot);

        assertEquals(0, run.baseline().exitCode());
        assertTrue(Files.exists(jacocoDir.resolve("jacoco.xml")));
        assertTrue(Files.exists(jacocoExec));
        assertFalse(Files.exists(jacocoDir.resolve("old.xml")));
    }

    @Test
    void parsesJacocoXmlWhenCoverageRunSucceeds() throws Exception {
        Path projectRoot = TestProjectFactory.createProject("coverage-runner-parse");

        CoverageRun run = new CoverageRunner(new ProcessCommandExecutor()).generateCoverage(projectRoot);

        assertTrue(run.report().covers("mutate4java/Sample.java", 5));
    }
}
