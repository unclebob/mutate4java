package mutate4java.project;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class SourceFileFinder {

    private SourceFileFinder() {
    }

    public static List<Path> findAllJavaFilesUnderSrc(Path root) throws IOException {
        Path src = root.resolve("src");
        if (!Files.isDirectory(src)) {
            return List.of();
        }
        try (var stream = Files.walk(src)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .toList();
        }
    }
}
