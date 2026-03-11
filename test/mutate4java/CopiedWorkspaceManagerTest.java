package mutate4java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CopiedWorkspaceManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void createsWorkerCopiesWithoutCopyingExistingTargetOutput() throws Exception {
        Path moduleRoot = tempDir.resolve("demo-module");
        Files.createDirectories(moduleRoot.resolve("src/main/java/demo"));
        Files.createDirectories(moduleRoot.resolve("target/classes"));
        Files.writeString(moduleRoot.resolve("pom.xml"), "<project/>");
        Files.writeString(moduleRoot.resolve("src/main/java/demo/App.java"), "class App {}");
        Files.writeString(moduleRoot.resolve("target/classes/App.class"), "compiled");

        try (WorkerWorkspaces workspaces = new CopiedWorkspaceManager().createWorkerWorkspaces(moduleRoot, 2)) {
            Path workerRoot = workspaces.workerRoots().get(0);
            assertTrue(Files.exists(workerRoot.resolve("pom.xml")));
            assertTrue(Files.exists(workerRoot.resolve("src/main/java/demo/App.java")));
            assertFalse(Files.exists(workerRoot.resolve("target/classes/App.class")));
        }
    }

    @Test
    void cleansUpWorkerRunDirectoryOnClose() throws Exception {
        Path moduleRoot = tempDir.resolve("demo-module");
        Files.createDirectories(moduleRoot.resolve("src/main/java/demo"));
        Files.writeString(moduleRoot.resolve("pom.xml"), "<project/>");
        Files.writeString(moduleRoot.resolve("src/main/java/demo/App.java"), "class App {}");

        WorkerWorkspaces workspaces = new CopiedWorkspaceManager().createWorkerWorkspaces(moduleRoot, 1);
        Path runRoot = workspaces.runRoot();
        Path nestedTargetFile = runRoot.resolve("worker-1/target/surefire-reports/result.txt");
        Files.createDirectories(nestedTargetFile.getParent());
        Files.writeString(nestedTargetFile, "done");

        assertTrue(Files.exists(runRoot));
        workspaces.close();

        assertFalse(Files.exists(runRoot));
    }
}
