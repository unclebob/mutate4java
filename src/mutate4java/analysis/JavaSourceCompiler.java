package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Trees;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static javax.tools.JavaCompiler.CompilationTask;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;

final class JavaSourceCompiler {

    CompiledSource compile(Path file) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.ROOT, null)) {
            Path outputDir = Files.createTempDirectory("mutate4java-classes");
            fileManager.setLocationFromPaths(CLASS_OUTPUT, List.of(outputDir));
            fileManager.setLocationFromPaths(CLASS_PATH, List.of());
            Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjects(file.toFile());
            CompilationTask task = compiler.getTask(null, fileManager, diagnostics,
                    List.of("-proc:none", "-implicit:none"), null, javaFiles);
            JavacTask javacTask = (JavacTask) task;
            List<CompilationUnitTree> units = new ArrayList<>();
            for (CompilationUnitTree unit : javacTask.parse()) {
                units.add(unit);
            }
            javacTask.analyze();
            return new CompiledSource(units, Trees.instance(javacTask));
        }
    }
}
