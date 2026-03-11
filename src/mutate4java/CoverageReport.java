package mutate4java;

import java.util.Set;

record CoverageReport(Set<CoverageSite> coveredLines) {

    boolean covers(String sourcePath, int lineNumber) {
        return coveredLines.contains(new CoverageSite(sourcePath, lineNumber));
    }
}
