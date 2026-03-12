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

final class UsageText {

    private UsageText() {
    }

    static String text() {
        return """
                Usage:
                  mutate4java <file.java>                      Mutate one Java source file
                  mutate4java <file.java> --scan              Print mutation-site scan without running tests
                  mutate4java <file.java> --update-manifest   Write embedded manifest without running tests
                  mutate4java <file.java> --lines 12,18       Restrict mutations to specific source lines
                  mutate4java <file.java> --since-last-run    Mutate only scopes changed since embedded manifest
                  mutate4java <file.java> --mutate-all        Ignore embedded manifest and mutate all covered sites
                  mutate4java <file.java> --mutation-warning 50 Warn when selected mutation count exceeds threshold
                  mutate4java <file.java> --max-workers 4     Limit parallel worker count
                  mutate4java <file.java> --timeout-factor 15 Set mutant timeout as baseline multiplier
                  mutate4java <file.java> --test-command CMD  Override the test command used for baseline and mutants
                  mutate4java <file.java> --verbose           Print live worker progress
                  mutate4java --help                          Print this help message
                """;
    }
}
