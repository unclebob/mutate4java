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
            return new DifferentialSelection(analysis.sites(), false);
        }
        if (!parsed.sinceLastRun() && !parsed.lines().isEmpty()) {
            return new DifferentialSelection(analysis.sites(), false);
        }
        Set<String> changedScopes = changedScopeIds(sourceFile, analysis);
        if (changedScopes.isEmpty() && manifestSupport.read(sourceFile).isEmpty()) {
            return new DifferentialSelection(analysis.sites(), false);
        }
        if (changedScopes.isEmpty()) {
            return new DifferentialSelection(List.of(), true);
        }
        List<MutationSite> selected = analysis.sites().stream()
                .filter(site -> changedScopes.contains(site.scopeId()))
                .toList();
        return new DifferentialSelection(selected, false);
    }

    public Set<String> changedScopeIds(Path sourceFile, SourceAnalysis analysis) throws Exception {
        return changedScopeFinder.changedScopeIds(sourceFile, analysis);
    }
}
