package mutate4java;

import java.util.List;
import java.util.Set;

record CliArguments(CliMode mode,
                    List<String> fileArgs,
                    Set<Integer> lines,
                    int timeoutFactor,
                    int maxWorkers,
                    boolean verbose) {
}
