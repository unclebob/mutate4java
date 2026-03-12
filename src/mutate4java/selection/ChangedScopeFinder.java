package mutate4java.selection;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.coverage.*;
import mutate4java.manifest.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

final class ChangedScopeFinder {

    private final ManifestSupport manifestSupport;

    ChangedScopeFinder(ManifestSupport manifestSupport) {
        this.manifestSupport = manifestSupport;
    }

    Set<String> changedScopeIds(Path sourceFile, SourceAnalysis analysis) throws Exception {
        var manifest = manifestSupport.read(sourceFile);
        if (manifest.isEmpty()) {
            return Set.of();
        }
        DifferentialManifest previous = manifest.get();
        if (previous.moduleHash().equals(analysis.moduleHash())) {
            return Set.of();
        }
        Map<String, String> previousHashes = new LinkedHashMap<>();
        for (MutationScope scope : previous.scopes()) {
            previousHashes.put(scope.id(), scope.semanticHash());
        }
        Set<String> changedScopes = new LinkedHashSet<>();
        for (MutationScope scope : analysis.scopes()) {
            if (!scope.semanticHash().equals(previousHashes.get(scope.id()))) {
                changedScopes.add(scope.id());
            }
        }
        return Set.copyOf(changedScopes);
    }
}
