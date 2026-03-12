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
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainAcceptanceTest {

    @TempDir
    Path tempDir;

    @Test
    void printsHelp() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"--help"}, tempDir,
                new PrintStream(out), new PrintStream(new ByteArrayOutputStream()));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("mutate4java <file.java>"));
        assertTrue(out.toString().contains("--lines 12,18"));
    }

    @Test
    void rejectsDirectoryTargets() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"crap4java"}, tempDir,
                new PrintStream(out), new PrintStream(err));

        assertEquals(1, exit);
        assertTrue(err.toString().contains("target must be a .java file"));
        assertTrue(out.toString().contains("mutate4java <file.java>"));
    }

    @Test
    void mutatesARealMavenProject() throws Exception {
        writePassingProject(tempDir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Flag.java"}, tempDir,
                new PrintStream(out), new PrintStream(err));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("KILLED src/main/java/demo/Flag.java:5 replace true with false"));
        assertTrue(out.toString().contains("Coverage: 0 uncovered sites skipped."));
        assertTrue(out.toString().contains("Summary: 1 killed, 0 survived, 1 total."));
        assertEquals("", err.toString());
    }

    @Test
    void mutatesComparisonOperatorsInARealMavenProject() throws Exception {
        writeComparisonProject(tempDir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Threshold.java"}, tempDir,
                new PrintStream(out), new PrintStream(new ByteArrayOutputStream()));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("KILLED src/main/java/demo/Threshold.java:5 replace > with >="));
        assertTrue(out.toString().contains("Summary: 1 killed, 0 survived, 1 total."));
    }

    @Test
    void failsFastWhenBaselineProjectTestsAreRed() throws Exception {
        writeFailingProject(tempDir);
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Flag.java"}, tempDir,
                new PrintStream(new ByteArrayOutputStream()), new PrintStream(err));

        assertEquals(2, exit);
        assertTrue(err.toString().contains("Baseline tests failed."));
    }

    @Test
    void mutatesReferenceRvaluesToNullInARealMavenProject() throws Exception {
        writeNullProject(tempDir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Greeting.java"}, tempDir,
                new PrintStream(out), new PrintStream(new ByteArrayOutputStream()));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("KILLED src/main/java/demo/Greeting.java:5 replace \"hello\" with null"));
        assertTrue(out.toString().contains("Summary: 1 killed, 0 survived, 1 total."));
    }

    @Test
    void mutatesUnaryOperatorsInARealMavenProject() throws Exception {
        writeUnaryProject(tempDir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Guard.java"}, tempDir,
                new PrintStream(out), new PrintStream(new ByteArrayOutputStream()));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("KILLED src/main/java/demo/Guard.java:5 replace ! with removed !"));
        assertTrue(out.toString().contains("Summary: 1 killed, 0 survived, 1 total."));
    }

    @Test
    void restrictsMutationsToRequestedLinesInARealMavenProject() throws Exception {
        writeTwoMutationProject(tempDir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Pair.java", "--lines", "5"}, tempDir,
                new PrintStream(out), new PrintStream(new ByteArrayOutputStream()));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("replace true with false"));
        assertTrue(!out.toString().contains("replace false with true"));
        assertTrue(out.toString().contains("Summary: 1 killed, 0 survived, 1 total."));
    }

    @Test
    void killsTimedOutMutantsInARealMavenProject() throws Exception {
        writeTimeoutProject(tempDir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Looping.java", "--timeout-factor", "1"}, tempDir,
                new PrintStream(out), new PrintStream(new ByteArrayOutputStream()));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("replace ! with removed !"));
        assertTrue(out.toString().contains("timed out"));
    }

    @Test
    void reportsUncoveredSitesFromCoverageAndSkipsThem() throws Exception {
        writeUncoveredProject(tempDir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Covered.java"}, tempDir,
                new PrintStream(out), new PrintStream(new ByteArrayOutputStream()));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("UNCOVERED src/main/java/demo/Covered.java:9 replace false with true"));
        assertTrue(out.toString().contains("Coverage: 1 uncovered sites skipped."));
        assertTrue(out.toString().contains("Summary: 1 killed, 0 survived, 1 total."));
    }

    @Test
    void updatesManifestWithoutRunningProjectTests() throws Exception {
        writeFailingProject(tempDir);
        Path source = tempDir.resolve("src/main/java/demo/Flag.java");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        int exit = Main.run(new String[]{"src/main/java/demo/Flag.java", "--update-manifest"}, tempDir,
                new PrintStream(out), new PrintStream(err));

        assertEquals(0, exit);
        assertTrue(out.toString().contains("Updated manifest for src/main/java/demo/Flag.java"));
        assertEquals("", err.toString());
        assertTrue(new ManifestSupport().read(source).isPresent());
        assertTrue(Files.readString(source).contains("mutate4java-manifest"));
    }

    private void writePassingProject(Path root) throws Exception {
        Files.writeString(root.resolve("pom.xml"), pomXml());
        Path source = root.resolve("src/main/java/demo/Flag.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package demo;

                public class Flag {
                    public boolean enabled() {
                        return true;
                    }
                }
                """);
        Path test = root.resolve("src/test/java/demo/FlagTest.java");
        Files.createDirectories(test.getParent());
        Files.writeString(test, """
                package demo;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertTrue;

                class FlagTest {
                    @Test
                    void enabledByDefault() {
                        assertTrue(new Flag().enabled());
                    }
                }
                """);
    }

    private void writeFailingProject(Path root) throws Exception {
        writePassingProject(root);
        Path test = root.resolve("src/test/java/demo/FlagTest.java");
        Files.writeString(test, """
                package demo;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertFalse;

                class FlagTest {
                    @Test
                    void enabledByDefault() {
                        assertFalse(new Flag().enabled());
                    }
                }
                """);
    }

    private void writeComparisonProject(Path root) throws Exception {
        Files.writeString(root.resolve("pom.xml"), pomXml());
        Path source = root.resolve("src/main/java/demo/Threshold.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package demo;

                public class Threshold {
                    public boolean accepts(int value) {
                        return value > 10;
                    }
                }
                """);
        Path test = root.resolve("src/test/java/demo/ThresholdTest.java");
        Files.createDirectories(test.getParent());
        Files.writeString(test, """
                package demo;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertFalse;
                import static org.junit.jupiter.api.Assertions.assertTrue;

                class ThresholdTest {
                    @Test
                    void acceptsOnlyValuesAboveTen() {
                        Threshold threshold = new Threshold();
                        assertFalse(threshold.accepts(10));
                        assertTrue(threshold.accepts(11));
                    }
                }
                """);
    }

    private void writeNullProject(Path root) throws Exception {
        Files.writeString(root.resolve("pom.xml"), pomXml());
        Path source = root.resolve("src/main/java/demo/Greeting.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package demo;

                public class Greeting {
                    public String value() {
                        return "hello";
                    }
                }
                """);
        Path test = root.resolve("src/test/java/demo/GreetingTest.java");
        Files.createDirectories(test.getParent());
        Files.writeString(test, """
                package demo;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static org.junit.jupiter.api.Assertions.assertNotNull;

                class GreetingTest {
                    @Test
                    void returnsGreeting() {
                        String value = new Greeting().value();
                        assertNotNull(value);
                        assertEquals("hello", value);
                    }
                }
                """);
    }

    private void writeUnaryProject(Path root) throws Exception {
        Files.writeString(root.resolve("pom.xml"), pomXml());
        Path source = root.resolve("src/main/java/demo/Guard.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package demo;

                public class Guard {
                    public boolean allows(boolean blocked) {
                        return !blocked;
                    }
                }
                """);
        Path test = root.resolve("src/test/java/demo/GuardTest.java");
        Files.createDirectories(test.getParent());
        Files.writeString(test, """
                package demo;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertFalse;
                import static org.junit.jupiter.api.Assertions.assertTrue;

                class GuardTest {
                    @Test
                    void allowsOnlyWhenNotBlocked() {
                        Guard guard = new Guard();
                        assertTrue(guard.allows(false));
                        assertFalse(guard.allows(true));
                    }
                }
                """);
    }

    private void writeTwoMutationProject(Path root) throws Exception {
        Files.writeString(root.resolve("pom.xml"), pomXml());
        Path source = root.resolve("src/main/java/demo/Pair.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package demo;

                public class Pair {
                    public boolean first() {
                        return true;
                    }

                    public boolean second() {
                        return false;
                    }
                }
                """);
        Path test = root.resolve("src/test/java/demo/PairTest.java");
        Files.createDirectories(test.getParent());
        Files.writeString(test, """
                package demo;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertFalse;
                import static org.junit.jupiter.api.Assertions.assertTrue;

                class PairTest {
                    @Test
                    void checksBothValues() {
                        Pair pair = new Pair();
                        assertTrue(pair.first());
                        assertFalse(pair.second());
                    }
                }
                """);
    }

    private void writeTimeoutProject(Path root) throws Exception {
        Files.writeString(root.resolve("pom.xml"), pomXml());
        Path source = root.resolve("src/main/java/demo/Looping.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package demo;

                public class Looping {
                    public boolean finishes(boolean blocked) {
                        while (!blocked) {
                        }
                        return true;
                    }
                }
                """);
        Path test = root.resolve("src/test/java/demo/LoopingTest.java");
        Files.createDirectories(test.getParent());
        Files.writeString(test, """
                package demo;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertTrue;

                class LoopingTest {
                    @Test
                    void returnsWhenInitiallyBlocked() {
                        assertTrue(new Looping().finishes(true));
                    }
                }
                """);
    }

    private void writeUncoveredProject(Path root) throws Exception {
        Files.writeString(root.resolve("pom.xml"), pomXml());
        Path source = root.resolve("src/main/java/demo/Covered.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, """
                package demo;

                public class Covered {
                    public boolean exercised() {
                        return true;
                    }

                    public boolean notExercised() {
                        return false;
                    }
                }
                """);
        Path test = root.resolve("src/test/java/demo/CoveredTest.java");
        Files.createDirectories(test.getParent());
        Files.writeString(test, """
                package demo;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertTrue;

                class CoveredTest {
                    @Test
                    void coversOnlyOneMethod() {
                        assertTrue(new Covered().exercised());
                    }
                }
                """);
    }

    private String pomXml() {
        return """
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>demo</groupId>
                  <artifactId>demo</artifactId>
                  <version>1.0-SNAPSHOT</version>
                  <properties>
                    <maven.compiler.source>17</maven.compiler.source>
                    <maven.compiler.target>17</maven.compiler.target>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    <junit.version>5.10.2</junit.version>
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>org.junit.jupiter</groupId>
                      <artifactId>junit-jupiter</artifactId>
                      <version>${junit.version}</version>
                      <scope>test</scope>
                    </dependency>
                  </dependencies>
                  <build>
                    <plugins>
                      <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-surefire-plugin</artifactId>
                        <version>3.2.5</version>
                      </plugin>
                    </plugins>
                  </build>
                </project>
                """;
    }
}
