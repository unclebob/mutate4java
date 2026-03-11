package mutate4java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

final class ProcessTestCommandExecutor implements TestCommandExecutor {

    @Override
    public TestRun runTests(Path projectRoot, long timeoutMillis) throws IOException, InterruptedException {
        long start = System.nanoTime();
        Process process = new ProcessBuilder("mvn", "test")
                .directory(projectRoot.toFile())
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
        long durationMillis = (System.nanoTime() - start) / 1_000_000L;
        String output = readOutput(process);
        return new TestRun(exitCode, output, durationMillis, timedOut);
    }

    private boolean waitFor(Process process) throws InterruptedException {
        process.waitFor();
        return true;
    }

    private String readOutput(Process process) {
        try {
            return new String(process.getInputStream().readAllBytes());
        } catch (IOException ex) {
            return "";
        }
    }
}
