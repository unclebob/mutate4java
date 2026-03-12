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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ChangedFileDetector {

    private ChangedFileDetector() {
    }

    public static List<Path> changedJavaFilesUnderSrc(Path projectRoot) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("git", "-C", projectRoot.toString(), "status", "--porcelain")
                .redirectErrorStream(true)
                .start();

        int exit = process.waitFor();
        String output = new String(process.getInputStream().readAllBytes());
        if (exit != 0) {
            throw new IllegalStateException("git status failed: " + output);
        }

        List<Path> files = new ArrayList<>();
        for (String line : output.split("\\R")) {
            Path path = parseLine(projectRoot, line);
            if (path != null && path.startsWith(projectRoot.resolve("src").normalize())) {
                files.add(path);
            }
        }
        files.sort(Path::compareTo);
        return files;
    }

    private static Path parseLine(Path root, String line) {
        if (line == null || line.isBlank() || line.length() < 4) {
            return null;
        }
        String pathText = line.substring(3).trim();
        int renameMarker = pathText.indexOf(" -> ");
        String finalPath = renameMarker >= 0 ? pathText.substring(renameMarker + 4) : pathText;
        if (!finalPath.endsWith(".java")) {
            return null;
        }
        return root.resolve(finalPath).normalize();
    }
}
