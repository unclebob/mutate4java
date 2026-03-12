package mutate4java.exec;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

final class WorkerFutureCollector {

    List<MutationResult> collect(List<Future<List<MutationResult>>> futures, ExecutorService threadPool) throws Exception {
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
}
