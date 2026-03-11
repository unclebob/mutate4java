package mutate4java;

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
        assertEquals(10, parsed.timeoutFactor());
        assertEquals(Math.max(1, Runtime.getRuntime().availableProcessors() / 2), parsed.maxWorkers());
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
}
