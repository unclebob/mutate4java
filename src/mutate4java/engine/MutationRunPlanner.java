package mutate4java.engine;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.analysis.*;
import mutate4java.cli.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;
import mutate4java.selection.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.util.List;

final class MutationRunPlanner {

    private final DifferentialSelector selector;
    private final MutationCoverageFilter coverageFilter;
    private final MutationExecution mutationExecution;
    private final ExecutionMessages messages;
    private final LineFilter lineFilter;

    MutationRunPlanner(DifferentialSelector selector,
                       MutationCoverageFilter coverageFilter,
                       MutationExecution mutationExecution,
                       ExecutionMessages messages,
                       LineFilter lineFilter) {
        this.selector = selector;
        this.coverageFilter = coverageFilter;
        this.mutationExecution = mutationExecution;
        this.messages = messages;
        this.lineFilter = lineFilter;
    }

    MutantResultSummary run(CliArguments parsed, ExecutionContext context, TestRun baseline, CoverageReport coverage) throws Exception {
        DifferentialSelection differentialSelection = selector.select(context.sourceFile(), parsed, context.analysis());
        List<MutationSite> discovered = lineFilter.filter(differentialSelection.selected(), parsed.lines());
        CoverageSelection coverageSelection = coverageFilter.filter(context.moduleRoot(), discovered, coverage);
        String extra = messages.extraText(parsed, differentialSelection, coverageSelection);
        if (coverageSelection.covered().isEmpty()) {
            return new MutantResultSummary(context.sourceFile(), baseline, extra,
                    differentialSelection.surfaceArea(), coverageSelection.uncovered(), List.of());
        }

        long timeoutMillis = mutationExecution.timeoutMillis(baseline.durationMillis(), parsed.timeoutFactor());
        List<MutationResult> results = mutationExecution.run(context.moduleRoot(), coverageSelection.covered(),
                timeoutMillis, parsed.maxWorkers(), context.progressReporter(), context.executor());
        return new MutantResultSummary(context.sourceFile(), baseline, extra,
                differentialSelection.surfaceArea(), coverageSelection.uncovered(), results);
    }
}

/* mutate4java-manifest
version=1
moduleHash=ad031bc2c36ea17568ead7707b96108e9c329df67b7bc090d0ea7eb29af64e12
scope.0.id=Y2xhc3M6TXV0YXRpb25SdW5QbGFubmVyI011dGF0aW9uUnVuUGxhbm5lcjoyNQ
scope.0.kind=class
scope.0.startLine=25
scope.0.endLine=61
scope.0.semanticHash=99e57bc703d036384b936c168850b0e8bfe4396c05e0404ec407ffd44004051d
scope.1.id=ZmllbGQ6TXV0YXRpb25SdW5QbGFubmVyI2NvdmVyYWdlRmlsdGVyOjI4
scope.1.kind=field
scope.1.startLine=28
scope.1.endLine=28
scope.1.semanticHash=019da1de33973970ebc82647aaa15de283fee74a1bc05576672855e36b87d66f
scope.2.id=ZmllbGQ6TXV0YXRpb25SdW5QbGFubmVyI2xpbmVGaWx0ZXI6MzE
scope.2.kind=field
scope.2.startLine=31
scope.2.endLine=31
scope.2.semanticHash=75af0640b8903e6fd41e753ad6d488dbdc1a9c0b7535651d6669d7d53c74c315
scope.3.id=ZmllbGQ6TXV0YXRpb25SdW5QbGFubmVyI21lc3NhZ2VzOjMw
scope.3.kind=field
scope.3.startLine=30
scope.3.endLine=30
scope.3.semanticHash=8ae3592c0684ad4c54e119b1c20442328d1870a58c4741cbec69ef4db68b208c
scope.4.id=ZmllbGQ6TXV0YXRpb25SdW5QbGFubmVyI211dGF0aW9uRXhlY3V0aW9uOjI5
scope.4.kind=field
scope.4.startLine=29
scope.4.endLine=29
scope.4.semanticHash=ce450ade83c80e69aa5aefca849f19d57342fdf239825fd271e8a16aea779f0b
scope.5.id=ZmllbGQ6TXV0YXRpb25SdW5QbGFubmVyI3NlbGVjdG9yOjI3
scope.5.kind=field
scope.5.startLine=27
scope.5.endLine=27
scope.5.semanticHash=a436277abdb7a6ee715e4815ca019e5e16422373abca32b45079abcbb4095e05
scope.6.id=bWV0aG9kOk11dGF0aW9uUnVuUGxhbm5lciNjdG9yKDUpOjMz
scope.6.kind=method
scope.6.startLine=33
scope.6.endLine=43
scope.6.semanticHash=be2a4220dd2e9db27a1b19d9bf28e14619c18885c06ff6112b14dc55f8b5b469
scope.7.id=bWV0aG9kOk11dGF0aW9uUnVuUGxhbm5lciNydW4oNCk6NDU
scope.7.kind=method
scope.7.startLine=45
scope.7.endLine=60
scope.7.semanticHash=b80ac97fcab73c12fdac3d51450528838be7aeb34053402724684b25f52db1cc
*/
