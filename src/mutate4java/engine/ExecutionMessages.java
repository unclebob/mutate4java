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

final class ExecutionMessages {

    String extraText(CliArguments parsed,
                     DifferentialSelection differentialSelection,
                     CoverageSelection coverageSelection) {
        StringBuilder extra = new StringBuilder();
        appendDifferentialDiagnostics(differentialSelection, coverageSelection, extra);
        if (differentialSelection.unchangedModule()) {
            extra.append("No mutations need testing.\n");
        }
        if (coverageSelection.covered().size() > parsed.mutationWarning()) {
            extra.append("WARNING: Found ").append(coverageSelection.covered().size())
                    .append(" mutations. Consider splitting this module.\n");
        }
        return extra.toString();
    }

    private void appendDifferentialDiagnostics(DifferentialSelection differentialSelection,
                                               CoverageSelection coverageSelection,
                                               StringBuilder extra) {
        extra.append("Total mutation sites: ").append(differentialSelection.totalMutationSites()).append('\n');
        extra.append("Covered mutation sites: ").append(coverageSelection.covered().size()).append('\n');
        extra.append("Uncovered mutation sites: ").append(coverageSelection.uncovered().size()).append('\n');
        extra.append("Changed mutation sites: ").append(differentialSelection.changedMutationSites()).append('\n');
        extra.append("Manifest exists: ").append(differentialSelection.manifestExists()).append('\n');
        extra.append("Module hash changed: ").append(differentialSelection.moduleHashChanged()).append('\n');
        extra.append("Differential surface area: ").append(differentialSelection.differentialSurfaceArea()).append('\n');
        extra.append("Manifest-violating surface area: ")
                .append(differentialSelection.manifestViolatingSurfaceArea()).append('\n');
    }
}

/* mutate4java-manifest
version=1
moduleHash=64fb9181d2cae485c121296b371e800005dfbdb2f6e93302e8d7426b746ace83
scope.0.id=Y2xhc3M6RXhlY3V0aW9uTWVzc2FnZXMjRXhlY3V0aW9uTWVzc2FnZXM6MjM
scope.0.kind=class
scope.0.startLine=23
scope.0.endLine=53
scope.0.semanticHash=3f57eb61d953d9815d75f224e37f9f60a85432eec789b0fa49d1bdc23db3f60d
scope.1.id=bWV0aG9kOkV4ZWN1dGlvbk1lc3NhZ2VzI2FwcGVuZERpZmZlcmVudGlhbERpYWdub3N0aWNzKDMpOjQw
scope.1.kind=method
scope.1.startLine=40
scope.1.endLine=52
scope.1.semanticHash=ae3b1d62652dba75304c2efeb37a8fcb48c098df71ed27f677fffc766ff07b35
scope.2.id=bWV0aG9kOkV4ZWN1dGlvbk1lc3NhZ2VzI2N0b3IoMCk6MjM
scope.2.kind=method
scope.2.startLine=1
scope.2.endLine=53
scope.2.semanticHash=029a78c61c336f8f906f4e6f0c5ede7ced6807b49b695abd36318ba0273aaede
scope.3.id=bWV0aG9kOkV4ZWN1dGlvbk1lc3NhZ2VzI2V4dHJhVGV4dCgzKToyNQ
scope.3.kind=method
scope.3.startLine=25
scope.3.endLine=38
scope.3.semanticHash=12211109bf22ea1e233e6ab1948431da26e00c5f05ea8012234cbdee0d9f129b
*/
