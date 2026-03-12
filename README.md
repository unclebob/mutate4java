# mutate4java

`mutate4java` is a standalone mutation-testing tool for Java projects.

It targets one Java source file at a time, discovers mutation sites in that file, runs the module's tests, and reports which mutants were killed, survived, timed out, or were skipped because the target line was uncovered.

It also supports differential mutation through an embedded manifest comment at the end of the source file. When a manifest is present, `mutate4java` can skip unchanged declaration scopes instead of rerunning the entire file.

## What It Does

For a requested Java source file, `mutate4java`:

- runs the owning Maven module's tests with JaCoCo coverage enabled
- fails fast if the unmodified baseline is red
- discovers supported mutation sites from the Java AST
- fingerprints declaration scopes for differential mutation
- filters out uncovered mutation sites using the JaCoCo XML report
- applies each covered mutation
- reruns `mvn test` for each mutant
- reports killed and survived mutants in source order
- writes an embedded manifest footer after successful clean runs

Mutation runs can be isolated across multiple worker copies of the module so parallel mutants do not overwrite each other.

## Usage

```bash
# Mutate one Java source file
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java

# Print a mutation-site scan without running tests
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --scan

# Write or refresh the embedded manifest without running tests
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --update-manifest

# Restrict mutation to specific lines
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --lines 12,18

# Mutate only scopes changed since the embedded manifest
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --since-last-run

# Ignore the embedded manifest and mutate all covered sites
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --mutate-all

# Warn when the selected mutation count is large
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --mutation-warning 50

# Limit parallel worker count
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --max-workers 4

# Adjust the mutant timeout multiplier
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --timeout-factor 15

# Override the test command
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --test-command "mvn test -DexcludeTags=no-mutate"

# Print live worker progress
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Flag.java --verbose

# Show usage
java -jar target/mutate4java-0.1.0-SNAPSHOT.jar --help
```

## Command-Line Options

- `--lines 12,18`
  Restricts mutation to the listed source lines in the requested file.

- `--scan`
  Bypasses baseline, coverage, and mutant execution. It prints every discovered mutation site and marks changed scopes from the embedded manifest with `*`.

- `--update-manifest`
  Rewrites the embedded manifest for the requested file without running baseline tests, coverage, or mutants.

- `--since-last-run`
  Restricts mutation to covered sites in declaration scopes that changed since the embedded manifest.

- `--mutate-all`
  Ignores the embedded manifest and runs all covered mutation sites.

- `--mutation-warning N`
  Prints a warning when the selected covered mutation count exceeds `N`. The default is `50`.

- `--max-workers N`
  Caps the number of isolated parallel workers. The default is half the available processors, with a minimum of `1`.

- `--timeout-factor N`
  Sets the timeout multiplier for each mutant test run, relative to the baseline duration. The default is `10`.

- `--test-command CMD`
  Overrides the baseline and mutant test command. When this is set, `mutate4java` falls back to treating all discovered sites as covered unless external coverage data is already available.

- `--verbose`
  Prints live mutation progress, including worker start and finish lines.

- `--help`
  Prints usage text.

## Targeting Rules

- The tool accepts exactly one `.java` file target.
- Directory-wide mutation is not supported.
- Test sources are executed, but they are not mutation targets.
- `--update-manifest` may not be combined with `--scan`, `--lines`, `--since-last-run`, or `--mutate-all`.
- `--lines` may not be combined with `--since-last-run` or `--mutate-all`.
- `--scan` may not be combined with `--since-last-run` or `--mutate-all`.
- `--since-last-run` may not be combined with `--mutate-all`.

## Coverage Filtering

`mutate4java` generates JaCoCo coverage during the baseline run and uses line coverage to skip uncovered mutation sites.

When `--test-command` is used, the tool does not attempt to wrap that custom command in JaCoCo. In that mode, mutation sites are treated as covered.

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

## Embedded Manifest

On successful clean runs, `mutate4java` writes an embedded footer comment at the end of the source file. That manifest records:

- manifest version
- module hash
- declaration scopes with stable ids
- start/end lines
- scope semantic hashes

The manifest is stripped before source analysis, so it does not perturb mutation-site positions or scope hashing.

With no explicit selection flags:

- if no manifest exists, `mutate4java` mutates all covered sites
- if a manifest exists and the module hash is unchanged, it runs zero mutations
- if a manifest exists and the module hash changed, it mutates only sites inside changed scopes

This makes repeated mutation runs cheaper on large files without relying on git.

`--update-manifest` is the manual version of that write step. It refreshes the embedded manifest from the current source analysis even if the module's tests are red, because it does not run them.

## Scan Mode

`--scan` prints a lightweight differential inventory for a single file. It does not:

- run the baseline tests
- generate coverage
- run any mutants
- rewrite the embedded manifest

Instead it prints the discovered mutation sites in source order and, when a manifest exists, prefixes sites in changed scopes with `*`.

Typical scan output looks like this:

```text
Scan: 2 mutation sites in src/main/java/demo/Flag.java
* src/main/java/demo/Flag.java:5 replace true with false
  src/main/java/demo/Flag.java:9 replace == with !=
* indicates a scope that differs from the embedded manifest.
```

## JUnit Tags

The default test command is:

```text
mvn test -DexcludeTags=no-mutate
```

That allows JUnit 5 tests tagged with `@Tag("no-mutate")` to be excluded from mutation baselines and mutant runs. This is useful for:

- tests that invoke mutation tools directly
- tests that recursively start Maven or coverage runs
- tests that are too expensive to include in every mutant cycle

Use `--test-command` if your project needs a different test-selection strategy.

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
WARNING: Found 72 mutations. Consider splitting this module.
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
mvn -pl tools/mutate4java verify
mvn -pl tools/mutate4java package
```

`mvn test` runs the fast unit suite. `mvn verify` also runs the Maven-spawning integration tests that exercise coverage generation and real `mvn test` execution against temporary sample projects.
