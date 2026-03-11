package mutate4java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class ProcessTestCommandExecutor implements TestCommandExecutor {

    private final ProcessLauncher launcher;

    ProcessTestCommandExecutor() {
        this(List.of("mvn", "test"));
    }

    ProcessTestCommandExecutor(List<String> command) {
        this(projectRoot -> startProcess(projectRoot, command));
    }

    ProcessTestCommandExecutor(ProcessLauncher launcher) {
        this.launcher = launcher;
    }

    @Override
    public TestRun runTests(Path projectRoot, long timeoutMillis) throws IOException, InterruptedException {
        long start = System.nanoTime();
        Process process = launcher.start(projectRoot);
        boolean timedOut = !waitFor(process, timeoutMillis);
        int exitCode = exitCode(process, timedOut);
        long durationMillis = (System.nanoTime() - start) / 1_000_000L;
        String output = readOutput(process);
        return new TestRun(exitCode, output, durationMillis, timedOut);
    }

    private static Process startProcess(Path projectRoot, List<String> command) throws IOException {
        return new ProcessBuilder(command)
                .directory(projectRoot.toFile())
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

    interface ProcessLauncher {
        Process start(Path projectRoot) throws IOException;
    }
}
