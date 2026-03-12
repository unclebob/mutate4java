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
        if (differentialSelection.unchangedModule()) {
            extra.append("No mutations need testing.\n");
        }
        if (coverageSelection.covered().size() > parsed.mutationWarning()) {
            extra.append("WARNING: Found ").append(coverageSelection.covered().size())
                    .append(" mutations. Consider splitting this module.\n");
        }
        return extra.toString();
    }
}
