package mutate4java;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

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
