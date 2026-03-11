package mutate4java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceFileFinderTest {

    @TempDir
    Path tempDir;

    @Test
    void returnsEmptyListWhenSrcDirectoryIsMissing() throws Exception {
        assertEquals(List.of(), SourceFileFinder.findAllJavaFilesUnderSrc(tempDir));
    }

    @Test
    void findsAndSortsJavaFilesUnderSrcOnly() throws Exception {
        Path src = tempDir.resolve("src/mutate4java");
        Files.createDirectories(src);
        Path first = src.resolve("A.java");
        Path second = src.resolve("nested/B.java");
        Path ignored = tempDir.resolve("test/mutate4java/C.java");
        Path notJava = src.resolve("notes.txt");

        Files.createDirectories(second.getParent());
        Files.createDirectories(ignored.getParent());
        Files.writeString(second, "class B {}");
        Files.writeString(first, "class A {}");
        Files.writeString(ignored, "class C {}");
        Files.writeString(notJava, "ignore");

        assertEquals(List.of(first, second), SourceFileFinder.findAllJavaFilesUnderSrc(tempDir));
    }
}
