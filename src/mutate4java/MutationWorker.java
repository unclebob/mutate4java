package mutate4java;

interface MutationWorker extends AutoCloseable {

    MutationResult run(MutationJob job) throws Exception;

    @Override
    default void close() throws Exception {
    }
}
