package mutate4java.report;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.nio.file.Path;
import java.util.List;

public final class ReportFormatter {

    public String format(Path projectRoot,
                         TestRun baseline,
                         String extra,
                         List<MutationSite> uncovered,
                         List<MutationResult> results) {
        StringBuilder out = new StringBuilder();
        out.append("Baseline tests passed in ").append(baseline.durationMillis()).append(" ms.\n");
        if (extra != null && !extra.isBlank()) {
            out.append(extra);
        }
        appendUncovered(projectRoot, uncovered, out);
        appendResults(projectRoot, results, out);
        appendSummary(uncovered, results, out);
        return out.toString();
    }

    private void appendUncovered(Path projectRoot, List<MutationSite> uncovered, StringBuilder out) {
        for (MutationSite site : uncovered) {
            out.append("UNCOVERED ");
            out.append(projectRoot.relativize(site.file())).append(':');
            out.append(site.lineNumber()).append(' ');
            out.append(site.description()).append('\n');
        }
    }

    private void appendResults(Path projectRoot, List<MutationResult> results, StringBuilder out) {
        for (MutationResult result : results) {
            out.append(result.killed() ? "KILLED " : "SURVIVED ");
            out.append(projectRoot.relativize(result.site().file())).append(':');
            out.append(result.site().lineNumber()).append(' ');
            out.append(result.site().description()).append(" (");
            out.append(result.durationMillis()).append(" ms)\n");
            if (result.timedOut()) {
                out.append("  timed out\n");
            }
        }
    }

    private void appendSummary(List<MutationSite> uncovered, List<MutationResult> results, StringBuilder out) {
        long killed = results.stream().filter(MutationResult::killed).count();
        long survived = results.size() - killed;
        out.append("Coverage: ").append(uncovered.size()).append(" uncovered sites skipped.\n");
        out.append("Summary: ").append(killed).append(" killed, ");
        out.append(survived).append(" survived, ");
        out.append(results.size()).append(" total.\n");
    }

}

/* mutate4java-manifest
version=1
moduleHash=15d74e4c5d996b54147158713ae54b5d4d0bb377ec10a1d1bb98119a3f37966c
scope.0.id=Y2xhc3M6UmVwb3J0Rm9ybWF0dGVyI1JlcG9ydEZvcm1hdHRlcjoxOA
scope.0.kind=class
scope.0.startLine=18
scope.0.endLine=67
scope.0.semanticHash=4d046587c4d891d02c02fc2d4ad9f3833c56a7a60967d9fec445dba528a758ea
scope.1.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNhcHBlbmRSZXN1bHRzKDMpOjQ1
scope.1.kind=method
scope.1.startLine=45
scope.1.endLine=56
scope.1.semanticHash=62f7942c31828587fc6c477fff8345b9eddcd313a4b8dad8fe3b247dbd858ee5
scope.2.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNhcHBlbmRTdW1tYXJ5KDMpOjU4
scope.2.kind=method
scope.2.startLine=58
scope.2.endLine=65
scope.2.semanticHash=73d37b78de840acee322910abcb25e0f4575600c0d9704293764f82c29b703dd
scope.3.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNhcHBlbmRVbmNvdmVyZWQoMyk6MzY
scope.3.kind=method
scope.3.startLine=36
scope.3.endLine=43
scope.3.semanticHash=a7c5c66742587ea912205331f694e0d5ba310c469811e0dbba29b8a1144420c1
scope.4.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNjdG9yKDApOjE4
scope.4.kind=method
scope.4.startLine=1
scope.4.endLine=67
scope.4.semanticHash=83a81117689cc070d73c4df862fd9f0a4f00db1dc8dd90085e3075c29b3effdd
scope.5.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNmb3JtYXQoNSk6MjA
scope.5.kind=method
scope.5.startLine=20
scope.5.endLine=34
scope.5.semanticHash=2fa801f0e8777771475cbf5e2c3ef76604701c4a4d83dbd98ca3d3e73a8d5510
*/
