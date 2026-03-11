package mutate4java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

final class SourceFileFinder {

    private SourceFileFinder() {
    }

    static List<Path> findAllJavaFilesUnderSrc(Path root) throws IOException {
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
