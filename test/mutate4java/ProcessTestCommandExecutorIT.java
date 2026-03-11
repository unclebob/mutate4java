package mutate4java;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessTestCommandExecutorIT {

    @Test
    void runsMavenTestsInProject() throws Exception {
        Path projectRoot = TestProjectFactory.createProject("process-test-executor");

        TestRun result = new ProcessTestCommandExecutor().runTests(projectRoot, 0);

        assertEquals(0, result.exitCode());
        assertFalse(result.timedOut());
        assertTrue(Files.exists(projectRoot.resolve("target/surefire-reports")));
    }

    @Test
    void timesOutLongRunningMavenTests() throws Exception {
        Path projectRoot = TestProjectFactory.createProject("process-test-timeout");
        Files.writeString(projectRoot.resolve("test/mutate4java/SampleTest.java"), """
                package mutate4java;

                import org.junit.jupiter.api.Test;

                class SampleTest {
                    @Test
                    void waitsTooLong() throws Exception {
                        Thread.sleep(5000);
                    }
                }
                """);

        TestRun result = new ProcessTestCommandExecutor().runTests(projectRoot, 50);

        assertEquals(124, result.exitCode());
        assertTrue(result.timedOut());
    }
}
