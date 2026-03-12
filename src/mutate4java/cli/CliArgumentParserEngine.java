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

final class CliArgumentParserEngine {

    private final CliArgumentValidators validators;

    CliArgumentParserEngine(CliArgumentValidators validators) {
        this.validators = validators;
    }

    CliArguments parse(String[] args) {
        CliArgumentParseState state = new CliArgumentParseState();
        for (int i = 0; i < args.length; i++) {
            i = CliArgumentSwitch.parse(args, i, state, validators);
        }
        if (state.help) {
            return state.helpArguments();
        }
        validators.validateSelectionFlags(state);
        validators.ensureExactlyOneJavaFile(state.values);
        return state.toCliArguments();
    }
}
