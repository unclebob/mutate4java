package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.util.List;

public interface WorkerPool extends AutoCloseable {

    List<MutationResult> runAll(List<MutationJob> jobs) throws Exception;
}
