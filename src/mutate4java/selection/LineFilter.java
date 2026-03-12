package mutate4java.selection;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.util.List;
import java.util.Set;

public final class LineFilter {

    public List<MutationSite> filter(List<MutationSite> sites, Set<Integer> lines) {
        if (lines.isEmpty()) {
            return sites;
        }
        return sites.stream().filter(site -> lines.contains(site.lineNumber())).toList();
    }
}
