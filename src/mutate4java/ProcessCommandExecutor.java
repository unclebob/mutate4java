package mutate4java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class ProcessCommandExecutor {

    CommandResult run(List<String> command, Path workingDirectory) throws IOException, InterruptedException {
        return run(command, workingDirectory, 0);
    }

    CommandResult run(List<String> command, Path workingDirectory, long timeoutMillis) throws IOException, InterruptedException {
        long start = System.nanoTime();
        Process process = startProcess(command, workingDirectory);
        boolean timedOut = !waitFor(process, timeoutMillis);
        int exitCode = exitCode(process, timedOut);
        String output = readOutput(process);
        long durationMillis = (System.nanoTime() - start) / 1_000_000L;
        return new CommandResult(exitCode, output, durationMillis, timedOut);
    }

    private Process startProcess(List<String> command, Path workingDirectory) throws IOException {
        return new ProcessBuilder(command)
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
    }

    private boolean waitFor(Process process, long timeoutMillis) throws InterruptedException {
        if (timeoutMillis <= 0) {
            process.waitFor();
            return true;
        }
        return process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    private int exitCode(Process process, boolean timedOut) throws InterruptedException {
        if (!timedOut) {
            return process.exitValue();
        }
        process.destroyForcibly();
        process.waitFor();
        return 124;
    }

    private String readOutput(Process process) {
        try {
            return new String(process.getInputStream().readAllBytes());
        } catch (IOException ex) {
            return "";
        }
    }
}
