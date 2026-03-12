package mutate4java;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliArgumentsParserTest {

    @Test
    void rejectsMissingFileArgument() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[0]));

        assertEquals("mutate4java requires exactly one Java file", error.getMessage());
    }

    @Test
    void parsesSingleExplicitFileArgument() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java"});

        assertEquals(CliMode.EXPLICIT_FILES, parsed.mode());
        assertEquals(List.of("src/main/java/demo/App.java"), parsed.fileArgs());
        assertEquals(Set.of(), parsed.lines());
        assertEquals(false, parsed.scan());
        assertEquals(false, parsed.updateManifest());
        assertEquals(false, parsed.sinceLastRun());
        assertEquals(false, parsed.mutateAll());
        assertEquals(10, parsed.timeoutFactor());
        assertEquals(50, parsed.mutationWarning());
        assertEquals(Math.max(1, Runtime.getRuntime().availableProcessors() / 2), parsed.maxWorkers());
        assertEquals(null, parsed.testCommand());
        assertEquals(false, parsed.verbose());
    }

    @Test
    void parsesLineFilterAndTimeoutFactor() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{
                "src/main/java/demo/App.java", "--lines", "12,18", "--timeout-factor", "15"
        });

        assertEquals(CliMode.EXPLICIT_FILES, parsed.mode());
        assertEquals(Set.of(12, 18), parsed.lines());
        assertEquals(15, parsed.timeoutFactor());
    }

    @Test
    void parsesMaxWorkers() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{
                "src/main/java/demo/App.java", "--max-workers", "4"
        });

        assertEquals(4, parsed.maxWorkers());
    }

    @Test
    void parsesVerboseFlag() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{
                "src/main/java/demo/App.java", "--verbose"
        });

        assertEquals(true, parsed.verbose());
    }

    @Test
    void parsesScanFlag() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{
                "src/main/java/demo/App.java", "--scan"
        });

        assertEquals(true, parsed.scan());
    }

    @Test
    void parsesUpdateManifestFlag() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{
                "src/main/java/demo/App.java", "--update-manifest"
        });

        assertEquals(true, parsed.updateManifest());
    }

    @Test
    void parsesDifferentialFlagsAndTestCommand() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{
                "src/main/java/demo/App.java",
                "--since-last-run",
                "--mutation-warning", "75",
                "--test-command", "mvn test -DexcludeTags=no-mutate"
        });

        assertEquals(true, parsed.sinceLastRun());
        assertEquals(false, parsed.mutateAll());
        assertEquals(75, parsed.mutationWarning());
        assertEquals("mvn test -DexcludeTags=no-mutate", parsed.testCommand());
    }

    @Test
    void parsesMutateAllFlag() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{
                "src/main/java/demo/App.java", "--mutate-all"
        });

        assertEquals(true, parsed.mutateAll());
    }

    @Test
    void parsesHelpMode() {
        CliArguments parsed = CliArgumentsParser.parse(new String[]{"--help"});

        assertEquals(CliMode.HELP, parsed.mode());
    }

    @Test
    void rejectsMultipleFileArguments() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "src/main/java/demo/Other.java"}));

        assertEquals("mutate4java accepts exactly one Java file", error.getMessage());
    }

    @Test
    void rejectsLinesWithoutFileArgument() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"--lines", "12"}));

        assertEquals("mutate4java requires exactly one Java file", error.getMessage());
    }

    @Test
    void rejectsNonJavaTarget() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"crap4java"}));

        assertEquals("mutate4java target must be a .java file", error.getMessage());
    }

    @Test
    void rejectsNonPositiveTimeoutFactor() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--timeout-factor", "0"}));

        assertEquals("--timeout-factor must be a positive integer", error.getMessage());
    }

    @Test
    void rejectsNonPositiveMaxWorkers() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--max-workers", "0"}));

        assertEquals("--max-workers must be a positive integer", error.getMessage());
    }

    @Test
    void rejectsUnknownOption() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--bogus"}));

        assertEquals("Unknown option: --bogus", error.getMessage());
    }

    @Test
    void rejectsLinesCombinedWithSinceLastRun() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--lines", "5", "--since-last-run"}));

        assertEquals("--lines may not be combined with --since-last-run", error.getMessage());
    }

    @Test
    void rejectsLinesCombinedWithMutateAll() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--lines", "5", "--mutate-all"}));

        assertEquals("--lines may not be combined with --mutate-all", error.getMessage());
    }

    @Test
    void rejectsSinceLastRunCombinedWithMutateAll() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--since-last-run", "--mutate-all"}));

        assertEquals("--since-last-run may not be combined with --mutate-all", error.getMessage());
    }

    @Test
    void rejectsScanCombinedWithSinceLastRun() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--scan", "--since-last-run"}));

        assertEquals("--scan may not be combined with --since-last-run", error.getMessage());
    }

    @Test
    void rejectsScanCombinedWithUpdateManifest() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--scan", "--update-manifest"}));

        assertEquals("--scan may not be combined with --update-manifest", error.getMessage());
    }

    @Test
    void rejectsUpdateManifestCombinedWithSinceLastRun() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--update-manifest", "--since-last-run"}));

        assertEquals("--update-manifest may not be combined with --since-last-run", error.getMessage());
    }

    @Test
    void rejectsUpdateManifestCombinedWithMutateAll() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--update-manifest", "--mutate-all"}));

        assertEquals("--update-manifest may not be combined with --mutate-all", error.getMessage());
    }

    @Test
    void rejectsUpdateManifestCombinedWithLines() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--update-manifest", "--lines", "5"}));

        assertEquals("--update-manifest may not be combined with --lines", error.getMessage());
    }

    @Test
    void rejectsScanCombinedWithMutateAll() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--scan", "--mutate-all"}));

        assertEquals("--scan may not be combined with --mutate-all", error.getMessage());
    }

    @Test
    void rejectsMissingLinesValue() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--lines"}));

        assertEquals("--lines requires a value", error.getMessage());
    }

    @Test
    void rejectsBlankLinesValue() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--lines", ",,"}));

        assertEquals("--lines requires at least one line number", error.getMessage());
    }

    @Test
    void rejectsNonNumericLinesValue() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--lines", "a"}));

        assertEquals("--lines must be a positive integer", error.getMessage());
    }

    @Test
    void rejectsMissingTimeoutFactorValue() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--timeout-factor"}));

        assertEquals("--timeout-factor requires a value", error.getMessage());
    }

    @Test
    void rejectsMissingMaxWorkersValue() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--max-workers"}));

        assertEquals("--max-workers requires a value", error.getMessage());
    }

    @Test
    void rejectsBlankTestCommand() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"src/main/java/demo/App.java", "--test-command", "   "}));

        assertEquals("--test-command must not be blank", error.getMessage());
    }
}
