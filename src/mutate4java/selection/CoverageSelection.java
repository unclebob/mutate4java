package mutate4java.selection;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.util.List;

public record CoverageSelection(List<MutationSite> covered, List<MutationSite> uncovered) {
}
