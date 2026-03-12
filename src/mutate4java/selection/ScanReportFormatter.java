package mutate4java.selection;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public final class ScanReportFormatter {

    private final Path workspaceRoot;

    public ScanReportFormatter(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
    }

    public String format(Path sourceFile, List<MutationSite> sites, Set<String> changedScopes) {
        StringBuilder report = new StringBuilder();
        report.append("Scan: ").append(sites.size()).append(" mutation sites in ")
                .append(relative(sourceFile)).append('\n');
        for (MutationSite site : sites) {
            report.append(changedScopes.contains(site.scopeId()) ? "* " : "  ");
            report.append(relative(site.file())).append(':').append(site.lineNumber())
                    .append(' ').append(site.description()).append('\n');
        }
        if (!changedScopes.isEmpty()) {
            report.append("* indicates a scope that differs from the embedded manifest.\n");
        }
        return report.toString();
    }

    private String relative(Path file) {
        return workspaceRoot.relativize(file).toString().replace('\\', '/');
    }
}
