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
            return notDifferential(analysis);
        }
        if (!parsed.sinceLastRun() && !parsed.lines().isEmpty()) {
            return notDifferential(analysis);
        }
        ChangedScopes changedScopes = changedScopesFor(sourceFile, analysis);
        if (changedScopes.allScopeIds().isEmpty() && !changedScopes.manifestPresent()) {
            return notDifferential(analysis);
        }
        int changedMutationSites = mutationCount(analysis, changedScopes.allScopeIds());
        int differentialSurfaceArea = mutationCount(analysis, changedScopes.unregisteredScopeIds());
        int manifestViolatingSurfaceArea = mutationCount(analysis, changedScopes.manifestViolationScopeIds());
        if (changedScopes.allScopeIds().isEmpty()) {
            return new DifferentialSelection(List.of(), true,
                    changedScopes.manifestPresent(), changedScopes.moduleHashChanged(),
                    analysis.sites().size(), changedMutationSites, differentialSurfaceArea, manifestViolatingSurfaceArea);
        }
        List<MutationSite> selected = analysis.sites().stream()
                .filter(site -> changedScopes.allScopeIds().contains(site.scopeId()))
                .toList();
        return new DifferentialSelection(selected, false,
                changedScopes.manifestPresent(), changedScopes.moduleHashChanged(),
                analysis.sites().size(), changedMutationSites, differentialSurfaceArea, manifestViolatingSurfaceArea);
    }

    public Set<String> changedScopeIds(Path sourceFile, SourceAnalysis analysis) throws Exception {
        return changedScopesFor(sourceFile, analysis).allScopeIds();
    }

    private ChangedScopes changedScopesFor(Path sourceFile, SourceAnalysis analysis) throws Exception {
        return changedScopeFinder.changedScopes(sourceFile, analysis);
    }

    private int mutationCount(SourceAnalysis analysis, Set<String> scopeIds) {
        return (int) analysis.sites().stream()
                .filter(site -> scopeIds.contains(site.scopeId()))
                .count();
    }

    private DifferentialSelection notDifferential(SourceAnalysis analysis) {
        return new DifferentialSelection(analysis.sites(), false, false, false,
                analysis.sites().size(), 0, 0, 0);
    }
}

/* mutate4java-manifest
version=1
moduleHash=d7d95752a93f4428e8b6385e045107802d3d74f30a1a4bfd10a24f5c94444832
scope.0.id=Y2xhc3M6RGlmZmVyZW50aWFsU2VsZWN0b3IjRGlmZmVyZW50aWFsU2VsZWN0b3I6MTU
scope.0.kind=class
scope.0.startLine=15
scope.0.endLine=70
scope.0.semanticHash=1b95f936bbabe4098175284ebc281c0a8e39bf0f06509543b9087450d10a30d0
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
scope.3.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI2NoYW5nZWRTY29wZUlkcygyKTo1Mg
scope.3.kind=method
scope.3.startLine=52
scope.3.endLine=54
scope.3.semanticHash=97017321e0629b356dc4ab5a270dcfeaac6b5a1738919864ad209d4686303976
scope.4.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI2NoYW5nZWRTY29wZXNGb3IoMik6NTY
scope.4.kind=method
scope.4.startLine=56
scope.4.endLine=58
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
scope.7.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI25vdERpZmZlcmVudGlhbCgxKTo2Ng
scope.7.kind=method
scope.7.startLine=66
scope.7.endLine=69
scope.7.semanticHash=19a29382f3b6eb2f11ecfc23f86389dfa2d943766ac5c068f1205930eddf59b7
scope.8.id=bWV0aG9kOkRpZmZlcmVudGlhbFNlbGVjdG9yI3NlbGVjdCgzKToyNQ
scope.8.kind=method
scope.8.startLine=25
scope.8.endLine=50
scope.8.semanticHash=e8ee85712f30fd67e5678158a2b11f33fdcd0bda7eb1bd6912f1a2b6cb1088a6
*/
