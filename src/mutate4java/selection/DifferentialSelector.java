package mutate4java.selection;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public final class DifferentialSelector {

    private final ManifestSupport manifestSupport;
    private final ChangedScopeFinder changedScopeFinder;

    public DifferentialSelector(ManifestSupport manifestSupport) {
        this.manifestSupport = manifestSupport;
        this.changedScopeFinder = new ChangedScopeFinder(manifestSupport);
    }

    public DifferentialSelection select(Path sourceFile, CliArguments parsed, SourceAnalysis analysis) throws Exception {
        if (parsed.mutateAll()) {
            return new DifferentialSelection(analysis.sites(), false, DifferentialSurfaceArea.notReported());
        }
        if (!parsed.sinceLastRun() && !parsed.lines().isEmpty()) {
            return new DifferentialSelection(analysis.sites(), false, DifferentialSurfaceArea.notReported());
        }
        ChangedScopes changedScopes = changedScopesFor(sourceFile, analysis);
        if (changedScopes.allScopeIds().isEmpty() && !changedScopes.manifestPresent()) {
            return new DifferentialSelection(analysis.sites(), false, DifferentialSurfaceArea.notReported());
        }
        DifferentialSurfaceArea surfaceArea = surfaceArea(analysis, changedScopes);
        if (changedScopes.allScopeIds().isEmpty()) {
            return new DifferentialSelection(List.of(), true, surfaceArea);
        }
        List<MutationSite> selected = analysis.sites().stream()
                .filter(site -> changedScopes.allScopeIds().contains(site.scopeId()))
                .toList();
        return new DifferentialSelection(selected, false, surfaceArea);
    }

    public Set<String> changedScopeIds(Path sourceFile, SourceAnalysis analysis) throws Exception {
        return changedScopesFor(sourceFile, analysis).allScopeIds();
    }

    private ChangedScopes changedScopesFor(Path sourceFile, SourceAnalysis analysis) throws Exception {
        return changedScopeFinder.changedScopes(sourceFile, analysis);
    }

    private DifferentialSurfaceArea surfaceArea(SourceAnalysis analysis, ChangedScopes changedScopes) {
        int unregisteredMutations = mutationCount(analysis, changedScopes.unregisteredScopeIds());
        int manifestViolations = mutationCount(analysis, changedScopes.manifestViolationScopeIds());
        return new DifferentialSurfaceArea(changedScopes.manifestPresent(), unregisteredMutations, manifestViolations);
    }

    private int mutationCount(SourceAnalysis analysis, Set<String> scopeIds) {
        return (int) analysis.sites().stream()
                .filter(site -> scopeIds.contains(site.scopeId()))
                .count();
    }
}

/* mutate4java-manifest
version=1
moduleHash=b1c72fea5f7bc7e195cb2b3bf1457b6f807daf3fd15e5c2057123fc450782101
scope.0.id=Y2xhc3M6RGlmZmVyZW50aWFsU2VsZWN0b3IjRGlmZmVyZW50aWFsU2VsZWN0b3I6MTU
scope.0.kind=class
scope.0.startLine=15
scope.0.endLine=65
scope.0.semanticHash=748232b36be279f4a32a260e7dc9f32c1351c175ddab9c2859aa9e3ab4ea9112
scope.1.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0b3IjY2hhbmdlZFNjb3BlRmluZGVyOjE4
scope.1.kind=field
scope.1.startLine=18
scope.1.endLine=18
scope.1.semanticHash=fc4e59b94bc92b74030de30c51b4f1d01cb532c277ef308db1ad8e130ccba1e3
scope.2.id=ZmllbGQ6RGlmZmVyZW50aWFsU2VsZWN0b3IjbWFuaWZlc3RTdXBwb3J0OjE3
scope.2.kind=field
scope.2.startLine=17
scope.2.endLine=17
scope.2.semanticHash=3b18e7680a38456f29abcc80d1cf6ae7426fd744949d701cb129be20a8759a6a
scope.3.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI2NoYW5nZWRTY29wZUlkcygyKTo0Ng
scope.3.kind=method
scope.3.startLine=46
scope.3.endLine=48
scope.3.semanticHash=97017321e0629b356dc4ab5a270dcfeaac6b5a1738919864ad209d4686303976
scope.4.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI2NoYW5nZWRTY29wZXNGb3IoMik6NTA
scope.4.kind=method
scope.4.startLine=50
scope.4.endLine=52
scope.4.semanticHash=2ff2650f1d5f81c64502a51ec8b7852f2bb63fd973a5a7d58df32d601bb4821f
scope.5.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI2N0b3IoMSk6MjA
scope.5.kind=method
scope.5.startLine=20
scope.5.endLine=23
scope.5.semanticHash=53715d99a51cc343d443c7ce5f6034e14227e38078304e477c493b59b8ffbda4
scope.6.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI211dGF0aW9uQ291bnQoMik6NjA
scope.6.kind=method
scope.6.startLine=60
scope.6.endLine=64
scope.6.semanticHash=7c979c4e0f91a33cfdb4627fafc32d30ec0a1d12b18e71fff020f769f8e124ea
scope.7.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI3NlbGVjdCgzKToyNQ
scope.7.kind=method
scope.7.startLine=25
scope.7.endLine=44
scope.7.semanticHash=4fd311cd4e8532a8cf495c3638df9f88f204fccde12a5cda534524628dd797d3
scope.8.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI3N1cmZhY2VBcmVhKDIpOjU0
scope.8.kind=method
scope.8.startLine=54
scope.8.endLine=58
scope.8.semanticHash=2fd77fdd211847a45106b8ca854338a03f68c296c5d056c3ce2975819254bcc8
*/
