package mutate4java.model;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.util.Set;

public final class CoverageReport {

    private final Set<CoverageSite> coveredLines;
    private final boolean treatAllAsCovered;

    public CoverageReport(Set<CoverageSite> coveredLines) {
        this(coveredLines, false);
    }

    private CoverageReport(Set<CoverageSite> coveredLines, boolean treatAllAsCovered) {
        this.coveredLines = coveredLines;
        this.treatAllAsCovered = treatAllAsCovered;
    }

    public static CoverageReport allCovered() {
        return new CoverageReport(Set.of(), true);
    }

    public boolean covers(String sourcePath, int lineNumber) {
        return treatAllAsCovered || coveredLines.contains(new CoverageSite(sourcePath, lineNumber));
    }
}
