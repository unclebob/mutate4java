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
import java.util.List;

public final class ProjectLayout {

    private final Path workspaceRoot;
    private final ModuleRootFinder moduleRootFinder;
    private final SourcePathNormalizer sourcePathNormalizer;

    public ProjectLayout(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
        this.moduleRootFinder = new ModuleRootFinder(workspaceRoot);
        this.sourcePathNormalizer = new SourcePathNormalizer();
    }

    public Path explicitFile(String arg) {
        Path path = workspaceRoot.resolve(arg).normalize();
        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("mutate4java target must be a .java file");
        }
        return path;
    }

    public Path moduleRootFor(List<Path> files) {
        Path moduleRoot = moduleRootFinder.find(files.get(0));
        return moduleRoot == null ? workspaceRoot : moduleRoot;
    }

    public String sourceSuffix(Path moduleRoot, Path file) {
        return sourcePathNormalizer.normalize(moduleRoot, file);
    }
}
