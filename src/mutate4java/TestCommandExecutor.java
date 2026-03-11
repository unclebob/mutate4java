package mutate4java;

import java.io.IOException;
import java.nio.file.Path;

interface TestCommandExecutor {

    TestRun runTests(Path projectRoot, long timeoutMillis) throws IOException, InterruptedException;
}
