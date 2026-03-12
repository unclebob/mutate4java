package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

final class ProcessTestCommandFactory {

    private ProcessTestCommandFactory() {
    }

    static Process startProcess(Path projectRoot, List<String> command) throws IOException {
        return new ProcessBuilder(command)
                .directory(projectRoot.toFile())
                .redirectErrorStream(true)
                .start();
    }

    static Process startShellProcess(Path projectRoot, String commandText) throws IOException {
        return new ProcessBuilder("/bin/sh", "-lc", commandText)
                .directory(projectRoot.toFile())
                .redirectErrorStream(true)
                .start();
    }
}
