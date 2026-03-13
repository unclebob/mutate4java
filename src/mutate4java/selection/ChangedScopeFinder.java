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
            return new ChangedScopes(false, false, Set.of(), Set.of());
        }
        DifferentialManifest previous = manifest.get();
        if (previous.moduleHash().equals(analysis.moduleHash())) {
            return new ChangedScopes(true, false, Set.of(), Set.of());
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
        return new ChangedScopes(true, true, Set.copyOf(unregisteredScopes), Set.copyOf(manifestViolations));
    }
}

/* mutate4java-manifest
version=1
moduleHash=f38382c7e6832e47396ce3eee5cb4b66885d3530a31b5ced253a6076e2b07421
scope.0.id=Y2xhc3M6Q2hhbmdlZFNjb3BlRmluZGVyI0NoYW5nZWRTY29wZUZpbmRlcjoyMw
scope.0.kind=class
scope.0.startLine=23
scope.0.endLine=56
scope.0.semanticHash=2d845f821a6cf403e5d2de714db89be8a7bbf29e650af01fe24f8a5e6692fb6a
scope.1.id=ZmllbGQ6Q2hhbmdlZFNjb3BlRmluZGVyI21hbmlmZXN0U3VwcG9ydDoyNQ
scope.1.kind=field
scope.1.startLine=25
scope.1.endLine=25
scope.1.semanticHash=3b18e7680a38456f29abcc80d1cf6ae7426fd744949d701cb129be20a8759a6a
scope.2.id=bWV0aG9kOkNoYW5nZWRTY29wZUZpbmRlciNjaGFuZ2VkU2NvcGVzKDIpOjMx
scope.2.kind=method
scope.2.startLine=31
scope.2.endLine=55
scope.2.semanticHash=847d110df02628ef9d7f1b3a28a471acdcd87c75a081b1ebd01cfc895837834c
scope.3.id=bWV0aG9kOkNoYW5nZWRTY29wZUZpbmRlciNjdG9yKDEpOjI3
scope.3.kind=method
scope.3.startLine=27
scope.3.endLine=29
scope.3.semanticHash=a7ba28ae9642105bbc7bdf7c40a631eac5ae6df21a366e6a3c834a782d804aa5
*/
