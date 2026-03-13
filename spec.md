# mutate4java Specification

## 1. Purpose

`mutate4java` is a mutation-testing tool for Java source code.

It shall:

- accept exactly one Java source file as its target
- discover mutation sites in that file from the Java AST
- optionally use an embedded manifest to restrict work to changed declaration scopes
- optionally use line coverage to skip uncovered mutation sites
- print differential diagnostics before worker execution
- execute tests against each selected mutant
- report killed, survived, timed-out, and uncovered mutation sites
- update the embedded manifest after successful clean runs

`mutate4java` is designed for repeated use against a single source file rather than whole-directory or whole-module mutation.

## 2. Scope

This specification defines:

- the command-line contract
- the mutation-site discovery rules
- the manifest format and semantics
- coverage and selection behavior
- worker execution behavior
- report and exit-code behavior

This specification does not define:

- a stable machine-readable output format
- support for whole-directory mutation
- support for non-Maven builds
- support for mutation of test sources

## 3. Terminology

- `target file`
  The single `.java` file passed on the command line.

- `module root`
  The nearest ancestor directory of the target file that contains `pom.xml`. If no such directory exists, the process working root is the module root.

- `mutation site`
  A concrete source replacement candidate discovered from the Java AST.

- `scope`
  A declaration-level region used for differential mutation. Scopes include methods, constructors, field initializers, initializer blocks, and relevant type scopes.

- `manifest`
  The embedded footer comment at the end of the target file containing scope hashes.

- `baseline`
  The unmodified test run used to establish pass/fail status and timeout duration.

- `covered site`
  A mutation site whose source line is covered according to the JaCoCo line report, unless coverage is bypassed by custom test command rules.

## 4. Targeting Rules

The tool shall accept exactly one explicit `.java` file target.

The tool shall reject:

- zero file arguments
- more than one file argument
- directory targets
- non-`.java` file targets

The tool shall mutate only the target file.

The tool shall not mutate files under test source roots.

## 5. Command-Line Interface

### 5.1 Supported Forms

The tool shall support these forms:

- `mutate4java <file.java>`
- `mutate4java <file.java> --scan`
- `mutate4java <file.java> --update-manifest`
- `mutate4java <file.java> --lines 12,18`
- `mutate4java <file.java> --since-last-run`
- `mutate4java <file.java> --mutate-all`
- `mutate4java <file.java> --mutation-warning N`
- `mutate4java <file.java> --max-workers N`
- `mutate4java <file.java> --timeout-factor N`
- `mutate4java <file.java> --test-command CMD`
- `mutate4java <file.java> --verbose`
- `mutate4java --help`

Options may be combined unless prohibited by the conflict rules below.

### 5.2 Option Semantics

- `--help`
  Print usage text and exit successfully.

- `--scan`
  Discover mutation sites and print a scan report without running baseline tests, coverage, mutants, or manifest updates.

- `--update-manifest`
  Analyze the file and write or refresh the embedded manifest without running baseline tests, coverage, or mutants.

- `--lines`
  Restrict mutation to the listed source lines in the target file.

- `--since-last-run`
  Restrict mutation to sites whose scopes differ from the embedded manifest.

- `--mutate-all`
  Ignore the embedded manifest and select all covered sites.

- `--mutation-warning N`
  Emit a warning when the selected mutation count exceeds `N`.

- `--max-workers N`
  Limit concurrent isolated mutation workers to `N`.

- `--timeout-factor N`
  Set mutant timeout to `baseline-duration * N`.

- `--test-command CMD`
  Override the default baseline and mutant test command.

- `--verbose`
  Emit live progress messages.

### 5.3 Defaults

Unless explicitly overridden:

- timeout factor shall default to `10`
- mutation warning threshold shall default to `50`
- max workers shall default to `max(1, availableProcessors / 2)`
- test command shall default to excluding JUnit tag `no-mutate`

### 5.4 Option Conflict Rules

The tool shall reject these combinations:

- `--scan` with `--since-last-run`
- `--scan` with `--mutate-all`
- `--scan` with `--update-manifest`
- `--lines` with `--since-last-run`
- `--lines` with `--mutate-all`
- `--lines` with `--update-manifest`
- `--since-last-run` with `--mutate-all`
- `--update-manifest` with `--since-last-run`
- `--update-manifest` with `--mutate-all`

## 6. Source Analysis

Mutation discovery shall be AST-based.

The tool shall parse Java source using the JDK compiler tree APIs.

The tool shall strip any embedded manifest before:

- source parsing
- mutation discovery
- scope discovery
- scope hashing

### 6.1 Supported Mutation Set

The current mutation set shall include:

- boolean literals: `true` <-> `false`
- equality/comparison: `==`, `!=`, `<`, `<=`, `>`, `>=`
- arithmetic: `+` <-> `-`, `*` <-> `/`
- conditional boolean operators: `&&` <-> `||`
- unary operators:
  - `!expr` -> `expr`
  - `-expr` -> `expr`
- integer constants: `0` <-> `1`
- reference-valued rvalues replaced with `null`

### 6.2 Exclusions

The tool shall not treat the following as mutation sites:

- comments
- string literals as operator text
- char literals as operator text
- generic angle brackets as comparison operators
- embedded manifest content

## 7. Manifest

### 7.1 Storage

The manifest shall be stored as an embedded footer comment at the end of the target file.

### 7.2 Contents

The manifest shall record:

- manifest version
- module hash
- scope ids
- scope kinds
- scope start/end lines
- scope semantic hashes

### 7.3 Scope Identity

Scopes shall have stable declaration-based identities rather than line-based identities alone.

### 7.4 Manifest Updates

The tool shall write the manifest:

- after a successful mutation run with no surviving mutants
- after a successful run with no executed mutants
- when `--update-manifest` is used

The tool shall not write the manifest:

- after a baseline failure
- after any surviving mutant
- during `--scan`

## 8. Differential Selection

When no explicit selection flag is provided:

- if no manifest exists, all covered sites shall be selected
- if a manifest exists and the module hash is unchanged, no sites shall be selected
- if a manifest exists and the module hash changed, only sites in changed scopes shall be selected

When `--since-last-run` is provided:

- only sites in changed scopes shall be selected

When `--mutate-all` is provided:

- all covered sites shall be selected regardless of manifest state

When `--lines` is provided:

- only sites on those lines shall be selected

For differential runs, the tool shall distinguish:

- mutations in scopes not registered in the manifest
- mutations in scopes that were registered but whose semantic hash changed

## 9. Coverage

By default, the tool shall generate JaCoCo coverage during the baseline run.

Coverage shall be interpreted at line granularity.

Mutation sites on uncovered lines shall be reported as uncovered and skipped.

If all discovered sites are uncovered:

- no mutant test runs shall be executed
- the run shall still be considered successful

When `--test-command` is supplied:

- the tool shall not wrap the command in JaCoCo itself
- mutation sites shall be treated as covered unless external coverage handling is later added

## 10. Baseline and Test Execution

### 10.1 Default Command

The default test command shall exclude JUnit 5 tag `no-mutate`.

### 10.2 Baseline

In normal mutation mode, the tool shall execute a baseline run before mutating.

If the baseline fails:

- mutation shall stop immediately
- no mutants shall be executed
- the run shall exit with baseline-failure status

### 10.3 `--update-manifest`

When `--update-manifest` is used:

- the tool shall not execute the baseline
- the tool shall not generate coverage
- the tool shall not run mutants
- the tool shall still analyze the file and write the manifest

This mode shall succeed even if the project test suite is currently red.

## 11. Worker Model

When mutation execution is required, the tool may use isolated worker copies of the module.

When `max-workers > 1`, the tool shall:

- create worker-local copies of the module
- mutate only files inside each private copy
- run tests inside each worker copy
- restore the mutated file between jobs

Workers shall be isolated so that:

- source files do not collide
- Maven output directories do not collide
- Surefire artifacts do not collide
- JaCoCo artifacts do not collide

## 12. Timeout Behavior

Each mutant run shall use a timeout derived from the baseline duration and timeout factor.

If a mutant times out:

- it shall be reported as killed
- the report shall indicate timeout

## 13. Reporting

### 13.1 Normal Mutation Output

The report shall include:

- baseline duration
- a pre-worker diagnostics block
- optional warning text
- uncovered site lines
- one line per mutant result
- summary counts for killed, survived, and total executed mutants

The pre-worker diagnostics block shall include:

- total mutation sites
- covered mutation sites
- uncovered mutation sites
- changed mutation sites
- whether a manifest exists
- whether the module hash changed
- differential surface area
- manifest-violating surface area

`Differential surface area` shall mean the count of selected mutations in scopes not registered in the manifest.

`Manifest-violating surface area` shall mean the count of selected mutations in scopes that were registered in the manifest but whose semantic hash changed.

### 13.2 Scan Output

`--scan` shall print:

- the total discovered mutation-site count
- each site in source order
- `*` before sites whose scopes differ from the embedded manifest, when a manifest exists

### 13.3 Update-Manifest Output

`--update-manifest` shall print a success line indicating that the manifest was updated for the target file.

## 14. Exit Codes

The tool shall use these exit codes:

- `0`
  Success. All executed mutants were killed, no mutants needed execution, scan succeeded, or manifest update succeeded.

- `1`
  Command-line usage error.

- `2`
  Baseline tests failed.

- `3`
  At least one mutant survived.

## 15. Non-Goals

The current implementation is not required to support:

- directory-wide mutation
- non-Maven project execution
- persistent external manifest sidecars
- mutation of tests
- a stable JSON report format

## 16. Conformance

An implementation conforms to this specification if it satisfies the command-line, analysis, selection, execution, manifest, reporting, and exit-code rules above for the currently supported mutation set.
