package mutate4java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class TestProjectFactory {

    private TestProjectFactory() {
    }

    static Path createProject(String name) throws IOException {
        Path projectRoot = Files.createTempDirectory(name);
        Files.writeString(projectRoot.resolve("pom.xml"), """
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
                  </properties>
                  <dependencies>
                    <dependency>
                      <groupId>org.junit.jupiter</groupId>
                      <artifactId>junit-jupiter</artifactId>
                      <version>5.10.2</version>
                      <scope>test</scope>
                    </dependency>
                  </dependencies>
                  <build>
                    <sourceDirectory>${project.basedir}/src/mutate4java</sourceDirectory>
                    <testSourceDirectory>${project.basedir}/test/mutate4java</testSourceDirectory>
                    <plugins>
                      <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-surefire-plugin</artifactId>
                        <version>3.2.5</version>
                      </plugin>
                    </plugins>
                  </build>
                </project>
                """);
        Path sourceRoot = projectRoot.resolve("src/mutate4java");
        Path testRoot = projectRoot.resolve("test/mutate4java");
        Files.createDirectories(sourceRoot);
        Files.createDirectories(testRoot);
        Files.writeString(sourceRoot.resolve("Sample.java"), """
                package mutate4java;

                class Sample {
                    boolean truth() {
                        return true;
                    }
                }
                """);
        Files.writeString(testRoot.resolve("SampleTest.java"), """
                package mutate4java;

                import org.junit.jupiter.api.Test;

                import static org.junit.jupiter.api.Assertions.assertTrue;

                class SampleTest {
                    @Test
                    void truthIsTrue() {
                        assertTrue(new Sample().truth());
                    }
                }
                """);
        return projectRoot;
    }
}
