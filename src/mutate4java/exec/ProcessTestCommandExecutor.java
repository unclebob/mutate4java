package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class ProcessTestCommandExecutor implements TestCommandExecutor {

    private static final List<String> DEFAULT_COMMAND = List.of("mvn", "test", "-DexcludeTags=no-mutate");

    private final ProcessLauncher launcher;
    private final String commandText;
    public ProcessTestCommandExecutor() {
        this(DEFAULT_COMMAND);
    }
    public ProcessTestCommandExecutor(List<String> command) {
        this(projectRoot -> ProcessTestCommandFactory.startProcess(projectRoot, command), null);
    }
    public ProcessTestCommandExecutor(String commandText) {
        this(projectRoot -> ProcessTestCommandFactory.startShellProcess(projectRoot, commandText), commandText);
    }
    public ProcessTestCommandExecutor(ProcessLauncher launcher) {
        this(launcher, null);
    }

    private ProcessTestCommandExecutor(ProcessLauncher launcher, String commandText) {
        this.launcher = launcher;
        this.commandText = commandText;
    }

    @Override
    public TestRun runTests(Path projectRoot, long timeoutMillis) throws IOException, InterruptedException {
        long start = System.nanoTime();
        Process process = launcher.start(projectRoot);
        return TimedProcessRun.finish(process, timeoutMillis, start);
    }

    public interface ProcessLauncher {
        Process start(Path projectRoot) throws IOException;
    }

    @Override
    public TestCommandExecutor withCommand(String command) {
        return new ProcessTestCommandExecutor(command);
    }
}
