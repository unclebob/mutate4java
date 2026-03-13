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

    ChangedScopes changedScopes(Path sourceFile, SourceAnalysis analysis) throws Exception {
        var manifest = manifestSupport.read(sourceFile);
        if (manifest.isEmpty()) {
            return new ChangedScopes(false, Set.of(), Set.of());
        }
        DifferentialManifest previous = manifest.get();
        if (previous.moduleHash().equals(analysis.moduleHash())) {
            return new ChangedScopes(true, Set.of(), Set.of());
        }
        Map<String, String> previousHashes = new LinkedHashMap<>();
        for (MutationScope scope : previous.scopes()) {
            previousHashes.put(scope.id(), scope.semanticHash());
        }
        Set<String> unregisteredScopes = new LinkedHashSet<>();
        Set<String> manifestViolations = new LinkedHashSet<>();
        for (MutationScope scope : analysis.scopes()) {
            String previousHash = previousHashes.get(scope.id());
            if (previousHash == null) {
                unregisteredScopes.add(scope.id());
            } else if (!scope.semanticHash().equals(previousHash)) {
                manifestViolations.add(scope.id());
            }
        }
        return new ChangedScopes(true, Set.copyOf(unregisteredScopes), Set.copyOf(manifestViolations));
    }
}

/* mutate4java-manifest
version=1
moduleHash=631d26e2315dd7afcf8dcbf7d56780579f051759ba3bc0b9cf56d1530289640f
scope.0.id=Y2xhc3M6Q2hhbmdlZFNjb3BlRmluZGVyI0NoYW5nZWRTY29wZUZpbmRlcjoyMw
scope.0.kind=class
scope.0.startLine=23
scope.0.endLine=56
scope.0.semanticHash=fe48a04546a48ac665dbfc4b98058096ea08810cd5d38ef6b1c98c731786c140
scope.1.id=ZmllbGQ6Q2hhbmdlZFNjb3BlRmluZGVyI21hbmlmZXN0U3VwcG9ydDoyNQ
scope.1.kind=field
scope.1.startLine=25
scope.1.endLine=25
scope.1.semanticHash=3b18e7680a38456f29abcc80d1cf6ae7426fd744949d701cb129be20a8759a6a
scope.2.id=bWV0aG9kOkNoYW5nZWRTY29wZUZpbmRlciNjaGFuZ2VkU2NvcGVzKDIpOjMx
scope.2.kind=method
scope.2.startLine=31
scope.2.endLine=55
scope.2.semanticHash=07847d4c0bb5ff4a1523bde5fa195dc18317a4a7490c2d2ed5bfa0e8ca588e06
scope.3.id=bWV0aG9kOkNoYW5nZWRTY29wZUZpbmRlciNjdG9yKDEpOjI3
scope.3.kind=method
scope.3.startLine=27
scope.3.endLine=29
scope.3.semanticHash=a7ba28ae9642105bbc7bdf7c40a631eac5ae6df21a366e6a3c834a782d804aa5
*/
