package mutate4java.coverage;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.exec.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class JacocoLineCoverageParser {

    private JacocoLineCoverageParser() {
    }
    public static CoverageReport parse(Path jacocoXmlPath) {
        if (jacocoXmlPath == null || !Files.exists(jacocoXmlPath)) {
            return new CoverageReport(Set.of());
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            var builder = factory.newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));

            Document document = builder.parse(jacocoXmlPath.toFile());
            NodeList packages = document.getElementsByTagName("package");
            Set<CoverageSite> coveredLines = new HashSet<>();
            for (int i = 0; i < packages.getLength(); i++) {
                readPackage((Element) packages.item(i), coveredLines);
            }
            return new CoverageReport(Set.copyOf(coveredLines));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse JaCoCo XML: " + jacocoXmlPath, ex);
        }
    }

    private static void readPackage(Element packageElement, Set<CoverageSite> coveredLines) {
        String packageName = packageElement.getAttribute("name");
        NodeList children = packageElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (!(node instanceof Element sourceFile) || !"sourcefile".equals(sourceFile.getTagName())) {
                continue;
            }
            String sourcePath = packageName.isBlank()
                    ? sourceFile.getAttribute("name")
                    : packageName + "/" + sourceFile.getAttribute("name");
            readCoveredLines(sourceFile, sourcePath, coveredLines);
        }
    }

    private static void readCoveredLines(Element sourceFile, String sourcePath, Set<CoverageSite> coveredLines) {
        NodeList children = sourceFile.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (!(node instanceof Element line) || !"line".equals(line.getTagName())) {
                continue;
            }
            int coveredInstructions = parseInt(line.getAttribute("ci"));
            if (coveredInstructions <= 0) {
                continue;
            }
            coveredLines.add(new CoverageSite(sourcePath, parseInt(line.getAttribute("nr"))));
        }
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
