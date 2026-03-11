# mutate4java

`mutate4java` is a standalone mutation-testing tool for Java projects.

It targets one Java source file at a time, discovers mutation sites in that file, runs the module's tests, and reports which mutants were killed, survived, timed out, or were skipped because the target line was uncovered.

## What It Does

For a requested Java source file, `mutate4java`:

- runs the owning Maven module's tests with JaCoCo coverage enabled
- fails fast if the unmodified baseline is red
- discovers supported mutation sites from the Java AST
- filters out uncovered mutation sites using the JaCoCo XML report
- applies each covered mutation
- reruns `mvn test` for each mutant
- reports killed and survived mutants in source order

Mutation runs can be isolated across multiple worker copies of the module so parallel mutants do not overwrite each other.

## Usage

```bash
# Mutate one Java source file
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java

# Restrict mutation to specific lines
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --lines 12,18

# Limit parallel worker count
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --max-workers 4

# Adjust the mutant timeout multiplier
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --timeout-factor 15

# Print live worker progress
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --verbose

# Show usage
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar --help
```

## Command-Line Options

- `--lines 12,18`
  Restricts mutation to the listed source lines in the requested file.

- `--max-workers N`
  Caps the number of isolated parallel workers. The default is half the available processors, with a minimum of `1`.

- `--timeout-factor N`
  Sets the timeout multiplier for each mutant test run, relative to the baseline duration. The default is `10`.

- `--verbose`
  Prints live mutation progress, including worker start and finish lines.

- `--help`
  Prints usage text.

## Targeting Rules

- The tool accepts exactly one `.java` file target.
- Directory-wide mutation is not supported.
- Test sources are executed, but they are not mutation targets.

## Coverage Filtering

`mutate4java` generates JaCoCo coverage during the baseline run and uses line coverage to skip uncovered mutation sites.

Uncovered sites are reported as:

```text
UNCOVERED path/to/File.java:42 replace true with false
```

If every discovered site is uncovered, no mutants are executed.

## Parallel Workers

When `--max-workers` is greater than `1`, `mutate4java` creates isolated worker copies of the owning Maven module under:

```text
target/mutation-workers/worker-N/
```

Each worker:

- owns its own copied module tree
- mutates only files inside that private copy
- runs `mvn test` inside its own workspace
- restores the mutated file before taking the next job

This avoids collisions in source files, Maven `target/` output, Surefire artifacts, and JaCoCo output.

## Current Mutation Set

The tool currently mutates:

- boolean literals: `true` <-> `false`
- equality and comparison: `==`, `!=`, `<`, `<=`, `>`, `>=`
- arithmetic: `+` <-> `-`, `*` <-> `/`
- conditional boolean operators: `&&` <-> `||`
- unary operators: `!expr` -> `expr`, `-expr` -> `expr`
- integer constants: `0` <-> `1`
- reference-valued rvalues: replace with `null`

Mutation discovery is AST-based, so comments, string literals, char literals, and generic angle brackets are not treated as mutation sites.

## Output

Typical output looks like this:

```text
Baseline tests passed in 4666 ms.
KILLED src/main/java/demo/Flag.java:5 replace true with false (4686 ms)
UNCOVERED src/main/java/demo/Flag.java:12 replace == with !=
Coverage: 1 uncovered sites skipped.
Summary: 1 killed, 0 survived, 1 total.
```

Exit codes:

- `0`: all executed mutants were killed, or there were no covered sites to run
- `1`: command-line usage error
- `2`: baseline tests failed
- `3`: at least one mutant survived

## Build

From the repository root:

```bash
mvn -pl tools/mutate4java test
mvn -pl tools/mutate4java package
```
