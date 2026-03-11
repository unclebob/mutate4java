package mutate4java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

final class ProcessTestCommandExecutor implements TestCommandExecutor {

    @Override
    public TestRun runTests(Path projectRoot, long timeoutMillis) throws IOException, InterruptedException {
        long start = System.nanoTime();
        Process process = startProcess(projectRoot);
        boolean timedOut = !waitFor(process, timeoutMillis);
        int exitCode = exitCode(process, timedOut);
        long durationMillis = (System.nanoTime() - start) / 1_000_000L;
        String output = readOutput(process);
        return new TestRun(exitCode, output, durationMillis, timedOut);
    }

    private Process startProcess(Path projectRoot) throws IOException {
        return new ProcessBuilder("mvn", "test")
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
}
