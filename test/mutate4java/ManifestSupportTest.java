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
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManifestSupportTest {

    private final ManifestSupport manifestSupport = new ManifestSupport();

    @TempDir
    Path tempDir;

    @Test
    void writesReadsAndStripsEmbeddedManifest() throws Exception {
        Path sourceFile = tempDir.resolve("Sample.java");
        String source = """
                package demo;

                class Sample {
                    boolean truthy() {
                        return true;
                    }
                }
                """;
        Files.writeString(sourceFile, source);
        DifferentialManifest manifest = new DifferentialManifest(
                1,
                "module-hash",
                List.of(new MutationScope("method:demo.Sample#truthy():4", "method", 4, 6, "scope-hash"))
        );

        manifestSupport.write(sourceFile, source, manifest);

        String withManifest = Files.readString(sourceFile);
        assertTrue(withManifest.contains("mutate4java-manifest"));
        assertEquals(source.stripTrailing() + "\n", manifestSupport.stripManifest(withManifest));

        DifferentialManifest parsed = manifestSupport.read(sourceFile).orElseThrow();
        assertEquals(1, parsed.version());
        assertEquals("module-hash", parsed.moduleHash());
        assertEquals(1, parsed.scopes().size());
        assertEquals("method:demo.Sample#truthy():4", parsed.scopes().get(0).id());
        assertEquals("method", parsed.scopes().get(0).kind());
        assertEquals(4, parsed.scopes().get(0).startLine());
        assertEquals(6, parsed.scopes().get(0).endLine());
        assertEquals("scope-hash", parsed.scopes().get(0).semanticHash());
    }
}
