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
        for (MutationSite site : uncovered) {
            out.append("UNCOVERED ");
            out.append(projectRoot.relativize(site.file())).append(':');
            out.append(site.lineNumber()).append(' ');
            out.append(site.description()).append('\n');
        }
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
        long killed = results.stream().filter(MutationResult::killed).count();
        long survived = results.size() - killed;
        out.append("Coverage: ").append(uncovered.size()).append(" uncovered sites skipped.\n");
        out.append("Summary: ").append(killed).append(" killed, ");
        out.append(survived).append(" survived, ");
        out.append(results.size()).append(" total.\n");
        return out.toString();
    }
}
