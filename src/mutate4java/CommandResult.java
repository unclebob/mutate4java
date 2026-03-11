package mutate4java;

record CommandResult(int exitCode, String output, long durationMillis, boolean timedOut) {
}
