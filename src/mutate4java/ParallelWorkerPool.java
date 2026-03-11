package mutate4java;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

final class ParallelWorkerPool implements WorkerPool {

    private final List<Path> workerRoots;
    private final TestCommandExecutor executor;
    private final ProgressReporter progressReporter;
    private final ExecutorService threadPool;

    ParallelWorkerPool(List<Path> workerRoots,
                       TestCommandExecutor executor,
                       ProgressReporter progressReporter) {
        this.workerRoots = workerRoots;
        this.executor = executor;
        this.progressReporter = progressReporter;
        this.threadPool = Executors.newFixedThreadPool(workerRoots.size());
    }

    @Override
    public List<MutationResult> runAll(List<MutationJob> jobs) throws Exception {
        progressReporter.runStarting(jobs.size(), workerRoots.size());
        Queue<MutationJob> queue = new ConcurrentLinkedQueue<>(jobs);
        List<Future<List<MutationResult>>> futures = new ArrayList<>();
        for (int i = 0; i < workerRoots.size(); i++) {
            int workerIndex = i + 1;
            Path workerRoot = workerRoots.get(i);
            futures.add(threadPool.submit(() -> runWorker(workerRoot, workerIndex, queue)));
        }

        List<MutationResult> results = new ArrayList<>();
        try {
            for (Future<List<MutationResult>> future : futures) {
                results.addAll(future.get());
            }
        } catch (ExecutionException ex) {
            threadPool.shutdownNow();
            Throwable cause = ex.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw new IllegalStateException("Worker execution failed", cause);
        }
        results.sort(Comparator.comparingInt(MutationResult::order));
        return List.copyOf(results);
    }

    private List<MutationResult> runWorker(Path workerRoot, int workerIndex, Queue<MutationJob> queue) throws Exception {
        List<MutationResult> results = new ArrayList<>();
        try (MutationWorker worker = new IsolatedMutationWorker(workerRoot, executor, progressReporter, workerIndex)) {
            for (MutationJob job = queue.poll(); job != null; job = queue.poll()) {
                results.add(worker.run(job));
            }
        }
        return results;
    }

    @Override
    public void close() {
        threadPool.shutdownNow();
    }
}
