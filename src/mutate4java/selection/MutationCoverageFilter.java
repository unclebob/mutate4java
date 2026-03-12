package mutate4java.selection;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.coverage.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class MutationCoverageFilter {

    private final ProjectLayout layout;

    public MutationCoverageFilter(ProjectLayout layout) {
        this.layout = layout;
    }

    public CoverageSelection filter(Path moduleRoot, List<MutationSite> sites, CoverageReport coverage) {
        List<MutationSite> covered = new ArrayList<>();
        List<MutationSite> uncovered = new ArrayList<>();
        for (MutationSite site : sites) {
            if (coverage.covers(layout.sourceSuffix(moduleRoot, site.file()), site.lineNumber())) {
                covered.add(site);
            } else {
                uncovered.add(site);
            }
        }
        return new CoverageSelection(List.copyOf(covered), List.copyOf(uncovered));
    }
}
