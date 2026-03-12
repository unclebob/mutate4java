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

import java.util.function.Consumer;

final class CliArgumentSwitch {

    private CliArgumentSwitch() {
    }

    static int parse(String[] args, int index, CliArgumentParseState state, CliArgumentValidators validators) {
        String arg = args[index];
        Integer parsedIndex = parseModeFlag(arg, index, state);
        if (parsedIndex != null) {
            return parsedIndex;
        }
        parsedIndex = parseSelectionFlag(arg, index, state);
        if (parsedIndex != null) {
            return parsedIndex;
        }
        parsedIndex = parseValueFlag(args, index, state, validators, arg);
        if (parsedIndex != null) {
            return parsedIndex;
        }
        return addFileArgument(arg, index, state);
    }

    private static Integer parseModeFlag(String arg, int index, CliArgumentParseState state) {
        return switch (arg) {
            case "--help" -> set(index, () -> state.help = true);
            case "--verbose" -> set(index, () -> state.verbose = true);
            default -> null;
        };
    }

    private static Integer parseSelectionFlag(String arg, int index, CliArgumentParseState state) {
        return switch (arg) {
            case "--scan" -> set(index, () -> state.scan = true);
            case "--update-manifest" -> set(index, () -> state.updateManifest = true);
            case "--since-last-run" -> set(index, () -> state.sinceLastRun = true);
            case "--mutate-all" -> set(index, () -> state.mutateAll = true);
            default -> null;
        };
    }

    private static Integer parseValueFlag(String[] args,
                                          int index,
                                          CliArgumentParseState state,
                                          CliArgumentValidators validators,
                                          String arg) {
        return switch (arg) {
            case "--lines" -> parseFlagValue(args, index, "--lines",
                    value -> state.lines = validators.parseLines(value), validators);
            case "--timeout-factor" -> parseFlagValue(args, index, "--timeout-factor",
                    value -> state.timeoutFactor = validators.parsePositiveInt(value, "--timeout-factor"), validators);
            case "--mutation-warning" -> parseFlagValue(args, index, "--mutation-warning",
                    value -> state.mutationWarning = validators.parsePositiveInt(value, "--mutation-warning"), validators);
            case "--max-workers" -> parseFlagValue(args, index, "--max-workers",
                    value -> state.maxWorkers = validators.parsePositiveInt(value, "--max-workers"), validators);
            case "--test-command" -> parseFlagValue(args, index, "--test-command",
                    value -> setTestCommand(state, value), validators);
            default -> null;
        };
    }

    private static int parseFlagValue(String[] args, int index, String flag, Consumer<String> consumer,
                                      CliArgumentValidators validators) {
        int valueIndex = index + 1;
        validators.ensureHasValue(args, valueIndex, flag);
        consumer.accept(args[valueIndex]);
        return valueIndex;
    }

    private static int addFileArgument(String arg, int index, CliArgumentParseState state) {
        if (arg.startsWith("--")) {
            throw new IllegalArgumentException("Unknown option: " + arg);
        }
        state.values.add(arg);
        return index;
    }

    private static int set(int index, Runnable action) {
        action.run();
        return index;
    }

    private static void setTestCommand(CliArgumentParseState state, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("--test-command must not be blank");
        }
        state.testCommand = value;
    }
}
