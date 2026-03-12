package mutate4java.selection;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.engine.*;

public final class ScanMode {

    private final DifferentialSelector selector;
    private final ScanReportFormatter formatter;
    private final LineFilter lineFilter;

    public ScanMode(DifferentialSelector selector, ScanReportFormatter formatter, LineFilter lineFilter) {
        this.selector = selector;
        this.formatter = formatter;
        this.lineFilter = lineFilter;
    }

    public String render(CliArguments parsed, ExecutionContext context) throws Exception {
        return formatter.format(
                context.sourceFile(),
                lineFilter.filter(context.analysis().sites(), parsed.lines()),
                selector.changedScopeIds(context.sourceFile(), context.analysis())
        );
    }
}
