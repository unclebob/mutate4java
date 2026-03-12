package mutate4java.cli;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.engine.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;
import mutate4java.selection.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class CliArgumentParseState {

    boolean help;
    boolean verbose;
    Set<Integer> lines = Set.of();
    boolean scan;
    boolean updateManifest;
    boolean sinceLastRun;
    boolean mutateAll;
    int timeoutFactor = CliArgumentsParserDefaults.DEFAULT_TIMEOUT_FACTOR;
    int mutationWarning = CliArgumentsParserDefaults.DEFAULT_MUTATION_WARNING;
    int maxWorkers = CliArgumentsParserDefaults.DEFAULT_MAX_WORKERS;
    String testCommand;
    final List<String> values = new ArrayList<>();

    CliArguments helpArguments() {
        return new CliArguments(CliMode.HELP, List.of(), Set.of(), false, false, false, false,
                timeoutFactor, mutationWarning, maxWorkers, null, verbose);
    }

    CliArguments toCliArguments() {
        return new CliArguments(
                CliMode.EXPLICIT_FILES,
                List.copyOf(values),
                lines,
                scan,
                updateManifest,
                sinceLastRun,
                mutateAll,
                timeoutFactor,
                mutationWarning,
                maxWorkers,
                testCommand,
                verbose
        );
    }
}
