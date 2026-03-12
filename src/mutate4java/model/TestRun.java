package mutate4java.model;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

public record TestRun(int exitCode, String output, long durationMillis, boolean timedOut) {

    public boolean passed() {
        return exitCode == 0 && !timedOut;
    }
}
