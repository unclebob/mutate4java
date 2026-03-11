package mutate4java;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.function.IntConsumer;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        int exit = run(args, Path.of(".").toAbsolutePath().normalize(), System.out, System.err);
        exitIfNeeded(exit, System::exit);
    }

    static int run(String[] args, Path projectRoot, PrintStream out, PrintStream err) throws Exception {
        return run(args, projectRoot, out, err, new ProcessTestCommandExecutor());
    }

    static int run(String[] args,
                   Path projectRoot,
                   PrintStream out,
                   PrintStream err,
                   TestCommandExecutor executor) throws Exception {
        return new CliApplication(projectRoot, out, err, executor).execute(args);
    }

    static String usage() {
        return """
                Usage:
                  mutate4java <file.java>                      Mutate one Java source file
                  mutate4java <file.java> --lines 12,18       Restrict mutations to specific source lines
                  mutate4java <file.java> --max-workers 4     Limit parallel worker count
                  mutate4java <file.java> --timeout-factor 15 Set mutant timeout as baseline multiplier
                  mutate4java <file.java> --verbose           Print live worker progress
                  mutate4java --help                          Print this help message
                """;
    }

    static void exitIfNeeded(int exit, IntConsumer exiter) {
        if (exit != 0) {
            exiter.accept(exit);
        }
    }
}
