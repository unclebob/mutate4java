package mutate4java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MutationCatalogTest {

    @TempDir
    Path tempDir;

    @Test
    void discoversBooleanEqualityAndComparisonMutations() throws Exception {
        Path file = tempDir.resolve("src/main/java/demo/Sample.java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, """
                package demo;

                class Sample {
                    boolean truthy() {
                        return true;
                    }

                    boolean same(int left, int right) {
                        return left == right;
                    }

                    boolean larger(int left, int right) {
                        return left > right;
                    }

                    boolean smaller(int left, int right) {
                        return left <= right;
                    }
                }
                """);

        List<MutationSite> sites = new MutationCatalog().discover(List.of(file));

        assertEquals(4, sites.size());
        assertEquals("replace true with false", sites.get(0).description());
        assertEquals("replace == with !=", sites.get(1).description());
        assertEquals("replace > with >=", sites.get(2).description());
        assertEquals("replace <= with <", sites.get(3).description());
    }

    @Test
    void ignoresOperatorsInsideStringsCharsAndComments() throws Exception {
        Path file = tempDir.resolve("src/main/java/demo/Literals.java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, """
                package demo;

                class Literals {
                    String text() {
                        return "true == false > <";
                    }

                    char angle() {
                        return '>';
                    }

                    boolean same(int left, int right) {
                        // left == right > 0
                        /* false != true <= >= */
                        return left == right;
                    }
                }
                """);

        List<MutationSite> sites = new MutationCatalog().discover(List.of(file));

        assertEquals(List.of(
                "replace \"true == false > <\" with null",
                "replace == with !="
        ), sites.stream().map(MutationSite::description).toList());
    }

    @Test
    void ignoresGenericTypeAngleBrackets() throws Exception {
        Path file = tempDir.resolve("src/main/java/demo/GenericSample.java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, """
                package demo;

                import java.util.List;

                class GenericSample {
                    List<String> names() {
                        return List.of("a");
                    }

                    boolean larger(int left, int right) {
                        return left > right;
                    }
                }
                """);

        List<MutationSite> sites = new MutationCatalog().discover(List.of(file));

        assertEquals(List.of(
                "replace List.of(\"a\") with null",
                "replace > with >="
        ), sites.stream().map(MutationSite::description).toList());
    }

    @Test
    void discoversArithmeticLogicalAndNullReplacementMutations() throws Exception {
        Path file = tempDir.resolve("src/main/java/demo/Expanded.java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, """
                package demo;

                class Expanded {
                    int add(int left, int right) {
                        return left + right;
                    }

                    int divide(int left, int right) {
                        return left / right;
                    }

                    boolean both(boolean left, boolean right) {
                        return left && right;
                    }

                    String message() {
                        return "hello";
                    }

                    String assign() {
                        String value = helper();
                        value = helper();
                        return value;
                    }

                    String helper() {
                        return "x";
                    }
                }
                """);

        List<MutationSite> sites = new MutationCatalog().discover(List.of(file));

        assertEquals(List.of(
                "replace + with -",
                "replace / with *",
                "replace && with ||",
                "replace \"hello\" with null",
                "replace helper() with null",
                "replace helper() with null",
                "replace value with null",
                "replace \"x\" with null"
        ), sites.stream().map(MutationSite::description).toList());
    }

    @Test
    void discoversUnaryAndConstantMutations() throws Exception {
        Path file = tempDir.resolve("src/main/java/demo/UnarySample.java");
        Files.createDirectories(file.getParent());
        Files.writeString(file, """
                package demo;

                class UnarySample {
                    boolean invert(boolean value) {
                        return !value;
                    }

                    int negative(int value) {
                        return -value;
                    }

                    int zero() {
                        return 0;
                    }

                    int one() {
                        return 1;
                    }
                }
                """);

        List<MutationSite> sites = new MutationCatalog().discover(List.of(file));

        assertEquals(List.of(
                "replace ! with removed !",
                "replace - with removed -",
                "replace 0 with 1",
                "replace 1 with 0"
        ), sites.stream().map(MutationSite::description).toList());

        MutationSite unaryNot = sites.get(0);
        assertEquals("", unaryNot.replacementText());

        MutationSite unaryMinus = sites.get(1);
        assertEquals("", unaryMinus.replacementText());
    }
}
