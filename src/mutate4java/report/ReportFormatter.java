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
                         DifferentialSurfaceArea surfaceArea,
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
        appendSurfaceArea(surfaceArea, out);
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

    private void appendSurfaceArea(DifferentialSurfaceArea surfaceArea, StringBuilder out) {
        if (!surfaceArea.reportable()) {
            return;
        }
        out.append("Surface area of the change: ");
        out.append(describeMutations(surfaceArea.unregisteredMutations(), "unregistered"));
        out.append(", ");
        out.append(describeMutations(surfaceArea.manifestViolations(), "manifest-violating"));
        out.append(".\n");
    }

    private String describeMutations(int count, String kind) {
        return count + " " + kind + " mutation" + (count == 1 ? "" : "s");
    }
}

/* mutate4java-manifest
version=1
moduleHash=edc04640fd3d099752b42a29f5968991727307ea481c4e769cd3231fe1e16fbb
scope.0.id=Y2xhc3M6UmVwb3J0Rm9ybWF0dGVyI1JlcG9ydEZvcm1hdHRlcjoxOA
scope.0.kind=class
scope.0.startLine=18
scope.0.endLine=83
scope.0.semanticHash=ff18d3d700d377a319e639f58e2bdad7373210c3cc255e0c6179984bb6bf2fde
scope.1.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNhcHBlbmRSZXN1bHRzKDMpOjQ3
scope.1.kind=method
scope.1.startLine=47
scope.1.endLine=58
scope.1.semanticHash=62f7942c31828587fc6c477fff8345b9eddcd313a4b8dad8fe3b247dbd858ee5
scope.2.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNhcHBlbmRTdW1tYXJ5KDMpOjYw
scope.2.kind=method
scope.2.startLine=60
scope.2.endLine=67
scope.2.semanticHash=73d37b78de840acee322910abcb25e0f4575600c0d9704293764f82c29b703dd
scope.3.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNhcHBlbmRTdXJmYWNlQXJlYSgyKTo2OQ
scope.3.kind=method
scope.3.startLine=69
scope.3.endLine=78
scope.3.semanticHash=d363980c336946101c861face8863a8976583410c480d6cf921eebe3cb4b5b5b
scope.4.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNhcHBlbmRVbmNvdmVyZWQoMyk6Mzg
scope.4.kind=method
scope.4.startLine=38
scope.4.endLine=45
scope.4.semanticHash=a7c5c66742587ea912205331f694e0d5ba310c469811e0dbba29b8a1144420c1
scope.5.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNjdG9yKDApOjE4
scope.5.kind=method
scope.5.startLine=1
scope.5.endLine=83
scope.5.semanticHash=d31b61988ee7c02855cba38a44c89dc42db8858435b3e804ba396b737ea2e5a6
scope.6.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNkZXNjcmliZU11dGF0aW9ucygyKTo4MA
scope.6.kind=method
scope.6.startLine=80
scope.6.endLine=82
scope.6.semanticHash=dd2f8081c2c83f62c4ce6412a5dfc47196ea675646c3716b538fc2d638e2070b
scope.7.id=bWV0aG9kOlJlcG9ydEZvcm1hdHRlciNmb3JtYXQoNik6MjA
scope.7.kind=method
scope.7.startLine=20
scope.7.endLine=36
scope.7.semanticHash=9efab74aa545c92e3afe9f5d2e76ef880a3f716169259151f6190bde02219f9d
*/
