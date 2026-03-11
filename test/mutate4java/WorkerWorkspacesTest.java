package mutate4java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class WorkerWorkspacesTest {

    @TempDir
    Path tempDir;

    @Test
    void closeDoesNothingWhenRunRootDoesNotExist() {
        WorkerWorkspaces workspaces = new WorkerWorkspaces(tempDir.resolve("missing"), List.of());

        assertDoesNotThrow(workspaces::close);
    }

    @Test
    void closeDeletesRunRootTree() throws Exception {
        Path runRoot = tempDir.resolve("run");
        Path nestedFile = runRoot.resolve("worker-1/target/surefire-reports/result.txt");
        Files.createDirectories(nestedFile.getParent());
        Files.writeString(nestedFile, "done");
        WorkerWorkspaces workspaces = new WorkerWorkspaces(runRoot, List.of(runRoot.resolve("worker-1")));

        workspaces.close();

        assertFalse(Files.exists(runRoot));
    }
}
