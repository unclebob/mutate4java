package mutate4java.project;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.nio.file.Files;
import java.nio.file.Path;

final class ModuleRootFinder {

    private final Path workspaceRoot;

    ModuleRootFinder(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
    }

    Path find(Path file) {
        Path current = Files.isDirectory(file) ? file : file.getParent();
        while (current != null && current.startsWith(workspaceRoot)) {
            if (Files.exists(current.resolve("pom.xml"))) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }
}
