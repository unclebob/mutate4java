package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class ProcessCommandExecutor {

    private final ProcessRunnerSupport support = new ProcessRunnerSupport();
    public CommandResult run(List<String> command, Path workingDirectory) throws IOException, InterruptedException {
        return run(command, workingDirectory, 0);
    }

    public CommandResult run(List<String> command, Path workingDirectory, long timeoutMillis) throws IOException, InterruptedException {
        long start = System.nanoTime();
        Process process = startProcess(command, workingDirectory);
        boolean timedOut = !support.waitFor(process, timeoutMillis);
        int exitCode = support.exitCode(process, timedOut);
        String output = support.readOutput(process);
        long durationMillis = (System.nanoTime() - start) / 1_000_000L;
        return new CommandResult(exitCode, output, durationMillis, timedOut);
    }

    private Process startProcess(List<String> command, Path workingDirectory) throws IOException {
        return new ProcessBuilder(command)
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
    }
}
