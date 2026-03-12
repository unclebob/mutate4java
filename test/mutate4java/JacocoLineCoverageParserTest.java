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

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacocoLineCoverageParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesCoveredLinesByPackageAndSourceFile() throws Exception {
        Path xml = tempDir.resolve("jacoco.xml");
        Files.writeString(xml, """
                <report name="demo">
                  <package name="demo">
                    <sourcefile name="Sample.java">
                      <line nr="5" mi="0" ci="3"/>
                      <line nr="9" mi="1" ci="0"/>
                    </sourcefile>
                  </package>
                </report>
                """);

        CoverageReport report = JacocoLineCoverageParser.parse(xml);

        assertTrue(report.covers("demo/Sample.java", 5));
        assertFalse(report.covers("demo/Sample.java", 9));
    }

    @Test
    void ignoresLinesWithMalformedCoverageNumbers() throws Exception {
        Path xml = tempDir.resolve("jacoco-invalid.xml");
        Files.writeString(xml, """
                <report name="demo">
                  <package name="demo">
                    <sourcefile name="Sample.java">
                      <line nr="oops" mi="0" ci="nan"/>
                    </sourcefile>
                  </package>
                </report>
                """);

        CoverageReport report = JacocoLineCoverageParser.parse(xml);

        assertFalse(report.covers("demo/Sample.java", 0));
    }
}
