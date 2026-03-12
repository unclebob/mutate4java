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

final class CliArgumentValidators {

    private final SelectionFlagValidator selection = new SelectionFlagValidator();
    private final JavaFileArgumentValidator javaFile = new JavaFileArgumentValidator();
    private final LineSelectionParser lines = new LineSelectionParser();
    private final IntegerArgumentParser integers = new IntegerArgumentParser();

    void validateSelectionFlags(CliArgumentParseState state) {
        selection.validate(state);
    }

    void ensureExactlyOneJavaFile(java.util.List<String> values) {
        javaFile.validate(values);
    }

    java.util.Set<Integer> parseLines(String text) {
        return lines.parse(text);
    }

    int parsePositiveInt(String text, String flag) {
        return integers.parsePositiveInt(text, flag);
    }

    void ensureHasValue(String[] args, int index, String flag) {
        if (index >= args.length || args[index].startsWith("--")) {
            throw new IllegalArgumentException(flag + " requires a value");
        }
    }
}
