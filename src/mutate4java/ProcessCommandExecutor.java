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
        Process process = new ProcessBuilder(command)
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
        boolean finished = timeoutMillis <= 0
                ? waitFor(process)
                : process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
        boolean timedOut = !finished;
        int exitCode;
        if (timedOut) {
            process.destroyForcibly();
            process.waitFor();
            exitCode = 124;
        } else {
            exitCode = process.exitValue();
        }
        String output = new String(process.getInputStream().readAllBytes());
        long durationMillis = (System.nanoTime() - start) / 1_000_000L;
        return new CommandResult(exitCode, output, durationMillis, timedOut);
    }

    private boolean waitFor(Process process) throws InterruptedException {
        process.waitFor();
        return true;
    }
}
