package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

interface MutationWorker extends AutoCloseable {

    MutationResult run(MutationJob job) throws Exception;

    @Override
    default void close() throws Exception {
    }
}
