package mutate4java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangedFileDetectorTest {

    @TempDir
    Path tempDir;

    @Test
    void findsModifiedAndUntrackedJavaFilesUnderSrc() throws Exception {
        run("git init", tempDir);
        run("git config user.email test@example.com", tempDir);
        run("git config user.name test", tempDir);

        Path src = tempDir.resolve("src/main/java/demo");
        Files.createDirectories(src);
        Path tracked = src.resolve("Tracked.java");
        Files.writeString(tracked, "class Tracked {}\n");

        run("git add .", tempDir);
        run("git commit -m init", tempDir);

        Files.writeString(tracked, "class Tracked { int x = 1; }\n");
        Path untracked = src.resolve("NewFile.java");
        Files.writeString(untracked, "class NewFile {}\n");
        Files.writeString(tempDir.resolve("README.md"), "ignore me\n");

        List<Path> changed = ChangedFileDetector.changedJavaFilesUnderSrc(tempDir);

        assertEquals(List.of(
                tempDir.resolve("src/main/java/demo/NewFile.java"),
                tempDir.resolve("src/main/java/demo/Tracked.java")
        ), changed);
    }

    private static void run(String command, Path dir) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("sh", "-c", command)
                .directory(dir.toFile())
                .redirectErrorStream(true)
                .start();
        if (process.waitFor() != 0) {
            throw new IllegalStateException(new String(process.getInputStream().readAllBytes()));
        }
    }
}
