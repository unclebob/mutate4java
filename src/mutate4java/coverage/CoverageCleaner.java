package mutate4java.coverage;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.exec.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class CoverageCleaner {

    void deleteStaleCoverage(Path directory, Path execFile) throws IOException {
        deleteTreeIfPresent(directory);
        deleteFileIfPresent(execFile);
    }

    private void deleteTreeIfPresent(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            return;
        }
        try (var walk = Files.walk(path)) {
            walk.sorted(java.util.Comparator.reverseOrder()).forEach(this::deletePath);
        }
    }

    private void deleteFileIfPresent(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            Files.deleteIfExists(path);
        }
    }

    private void deletePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed deleting stale coverage: " + path, ex);
        }
    }
}
