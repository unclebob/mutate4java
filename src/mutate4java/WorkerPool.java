package mutate4java;

import java.util.List;

interface WorkerPool extends AutoCloseable {

    List<MutationResult> runAll(List<MutationJob> jobs) throws Exception;
}
