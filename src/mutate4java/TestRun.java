package mutate4java;

record TestRun(int exitCode, String output, long durationMillis, boolean timedOut) {

    boolean passed() {
        return exitCode == 0 && !timedOut;
    }
}
