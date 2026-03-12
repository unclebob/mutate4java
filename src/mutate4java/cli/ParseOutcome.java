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

record ParseOutcome(CliArguments arguments, int exitCode) {

    static ParseOutcome ok(CliArguments arguments) {
        return new ParseOutcome(arguments, -1);
    }

    static ParseOutcome exit(int code) {
        return new ParseOutcome(null, code);
    }
}
