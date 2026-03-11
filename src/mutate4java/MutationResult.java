package mutate4java;

record MutationResult(MutationSite site, boolean killed, long durationMillis, boolean timedOut, int order, int totalJobs) {
}
