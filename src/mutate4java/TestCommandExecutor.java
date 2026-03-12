package mutate4java;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.io.IOException;
import java.nio.file.Path;

public interface TestCommandExecutor {

    TestRun runTests(Path projectRoot, long timeoutMillis) throws IOException, InterruptedException;

    default TestCommandExecutor withCommand(String command) {
        return this;
    }
}
