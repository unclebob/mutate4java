package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

final class TimedProcessRun {

    private TimedProcessRun() {
    }

    static TestRun finish(Process process, long timeoutMillis, long startNanos) throws InterruptedException {
        boolean timedOut = !waitFor(process, timeoutMillis);
        int exitCode = exitCode(process, timedOut);
        long durationMillis = (System.nanoTime() - startNanos) / 1_000_000L;
        return new TestRun(exitCode, readOutput(process), durationMillis, timedOut);
    }

    private static boolean waitFor(Process process, long timeoutMillis) throws InterruptedException {
        if (timeoutMillis <= 0) {
            process.waitFor();
            return true;
        }
        return process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    private static int exitCode(Process process, boolean timedOut) throws InterruptedException {
        if (!timedOut) {
            return process.exitValue();
        }
        process.destroyForcibly();
        process.waitFor();
        return 124;
    }

    private static String readOutput(Process process) {
        try {
            return new String(process.getInputStream().readAllBytes());
        } catch (IOException ex) {
            return "";
        }
    }
}
