package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

final class ModuleTreeCopier {

    void copy(Path moduleRoot, Path workerRoot) throws IOException {
        Path excludedRoot = moduleRoot.resolve("target").normalize();
        Files.walkFileTree(moduleRoot, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.normalize().startsWith(excludedRoot)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Files.createDirectories(workerRoot.resolve(moduleRoot.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, workerRoot.resolve(moduleRoot.relativize(file)));
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
