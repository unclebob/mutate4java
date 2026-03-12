package mutate4java.project;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.nio.file.Path;

final class SourcePathNormalizer {

    String normalize(Path moduleRoot, Path file) {
        String relative = moduleRoot.relativize(file).toString().replace('\\', '/');
        if (relative.startsWith("src/main/java/")) {
            return relative.substring("src/main/java/".length());
        }
        if (relative.startsWith("src/test/java/")) {
            return relative.substring("src/test/java/".length());
        }
        if (relative.startsWith("src/")) {
            return relative.substring("src/".length());
        }
        return relative;
    }
}
