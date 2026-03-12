package mutate4java.manifest;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ManifestSupport {

    private final ManifestBoundary boundary = new ManifestBoundary();
    private final ManifestCodec codec = new ManifestCodec();
    private final ManifestHashing hashing = new ManifestHashing();
    public Optional<DifferentialManifest> read(Path sourceFile) throws Exception {
        String raw = Files.readString(sourceFile);
        int start = boundary.startIndex(raw);
        if (start < 0) {
            return Optional.empty();
        }
        int end = raw.indexOf(ManifestBoundary.END, start);
        if (end < 0) {
            return Optional.empty();
        }
        String body = raw.substring(start + ManifestBoundary.START.length(), end).trim();
        return Optional.of(codec.parse(body));
    }
    public String stripManifest(String rawSource) {
        int start = boundary.startIndex(rawSource);
        if (start < 0) {
            return rawSource;
        }
        return rawSource.substring(0, start).stripTrailing() + "\n";
    }
    public void write(Path sourceFile, String sourceWithoutManifest, DifferentialManifest manifest) throws Exception {
        String updated = sourceWithoutManifest.stripTrailing() + "\n\n"
                + codec.serialize(manifest, ManifestBoundary.START, ManifestBoundary.END);
        Files.writeString(sourceFile, updated);
    }
    public String hashScopes(List<MutationScope> scopes) {
        return hashing.hashScopes(scopes);
    }
    public String hash(String text) {
        return hashing.hash(text);
    }
}
