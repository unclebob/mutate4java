package mutate4java;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class CliArgumentsParser {

    private static final int DEFAULT_TIMEOUT_FACTOR = 10;
    private static final int DEFAULT_MAX_WORKERS = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);

    private CliArgumentsParser() {
    }

    static CliArguments parse(String[] args) {
        boolean help = false;
        boolean verbose = false;
        Set<Integer> lines = Set.of();
        int timeoutFactor = DEFAULT_TIMEOUT_FACTOR;
        int maxWorkers = DEFAULT_MAX_WORKERS;
        List<String> values = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--help" -> help = true;
                case "--verbose" -> verbose = true;
                case "--lines" -> {
                    i++;
                    ensureHasValue(args, i, "--lines");
                    lines = parseLines(args[i]);
                }
                case "--timeout-factor" -> {
                    i++;
                    ensureHasValue(args, i, "--timeout-factor");
                    timeoutFactor = parsePositiveInt(args[i], "--timeout-factor");
                }
                case "--max-workers" -> {
                    i++;
                    ensureHasValue(args, i, "--max-workers");
                    maxWorkers = parsePositiveInt(args[i], "--max-workers");
                }
                default -> {
                    if (arg.startsWith("--")) {
                        throw new IllegalArgumentException("Unknown option: " + arg);
                    }
                    values.add(arg);
                }
            }
        }

        if (help) {
            return new CliArguments(CliMode.HELP, List.of(), Set.of(), timeoutFactor, maxWorkers, verbose);
        }

        ensureExactlyOneJavaFile(values);
        return new CliArguments(CliMode.EXPLICIT_FILES, List.copyOf(values), lines, timeoutFactor, maxWorkers, verbose);
    }

    private static void ensureHasValue(String[] args, int index, String flag) {
        if (index >= args.length || args[index].startsWith("--")) {
            throw new IllegalArgumentException(flag + " requires a value");
        }
    }

    private static Set<Integer> parseLines(String text) {
        Set<Integer> lines = new LinkedHashSet<>();
        for (String part : text.split(",")) {
            if (part.isBlank()) {
                continue;
            }
            lines.add(parsePositiveInt(part.trim(), "--lines"));
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("--lines requires at least one line number");
        }
        return Set.copyOf(lines);
    }

    private static int parsePositiveInt(String text, String flag) {
        try {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                throw new IllegalArgumentException(flag + " must be a positive integer");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(flag + " must be a positive integer");
        }
    }

    private static void ensureExactlyOneJavaFile(List<String> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("mutate4java requires exactly one Java file");
        }
        if (values.size() != 1) {
            throw new IllegalArgumentException("mutate4java accepts exactly one Java file");
        }
        if (!values.get(0).endsWith(".java")) {
            throw new IllegalArgumentException("mutate4java target must be a .java file");
        }
    }
}
