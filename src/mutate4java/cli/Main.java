package mutate4java.cli;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.engine.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;
import mutate4java.selection.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

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

    public static int run(String[] args, Path projectRoot, PrintStream out, PrintStream err) throws Exception {
        return run(args, projectRoot, out, err, new ProcessTestCommandExecutor());
    }

    public static int run(String[] args,
                          Path projectRoot,
                          PrintStream out,
                          PrintStream err,
                          TestCommandExecutor executor) throws Exception {
        return new CliApplication(projectRoot, out, err, executor).execute(args);
    }

    public static String usage() {
        return UsageText.text();
    }

    public static void exitIfNeeded(int exit, IntConsumer exiter) {
        if (exit != 0) {
            exiter.accept(exit);
        }
    }
}
