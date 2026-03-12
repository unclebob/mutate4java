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

public final class CliArgumentsParser {

    private static final CliArgumentParserEngine ENGINE = new CliArgumentParserEngine(new CliArgumentValidators());

    private CliArgumentsParser() {
    }

    public static CliArguments parse(String[] args) {
        return ENGINE.parse(args);
    }
}
