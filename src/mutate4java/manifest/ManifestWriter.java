package mutate4java.manifest;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.nio.file.Path;

public final class ManifestWriter {

    private final ManifestSupport manifestSupport;
    public ManifestWriter(ManifestSupport manifestSupport) {
        this.manifestSupport = manifestSupport;
    }
    public void write(Path sourceFile, SourceAnalysis analysis) throws Exception {
        manifestSupport.write(sourceFile, analysis.sourceWithoutManifest(),
                new DifferentialManifest(1, analysis.moduleHash(), analysis.scopes()));
    }
}
