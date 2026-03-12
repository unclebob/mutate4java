package mutate4java.manifest;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ManifestParser {

    DifferentialManifest parse(String body) {
        Map<Integer, Map<String, String>> scopes = new LinkedHashMap<>();
        int version = 1;
        String moduleHash = "";
        for (String line : body.split("\n")) {
            if (!line.isBlank()) {
                readScopeLine(scopes, line);
                if (line.startsWith("version=")) {
                    version = Integer.parseInt(line.substring("version=".length()));
                } else if (line.startsWith("moduleHash=")) {
                    moduleHash = line.substring("moduleHash=".length());
                }
            }
        }
        List<MutationScope> parsedScopes = scopes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> toScope(entry.getValue()))
                .toList();
        return new DifferentialManifest(version, moduleHash, parsedScopes);
    }

    private void readScopeLine(Map<Integer, Map<String, String>> scopes, String line) {
        int separator = line.indexOf('=');
        if (separator < 0) {
            return;
        }
        String key = line.substring(0, separator);
        if (!key.startsWith("scope.")) {
            return;
        }
        String[] parts = key.split("\\.");
        if (parts.length != 3) {
            return;
        }
        int index = Integer.parseInt(parts[1]);
        scopes.computeIfAbsent(index, ignored -> new LinkedHashMap<>()).put(parts[2], line.substring(separator + 1));
    }

    private MutationScope toScope(Map<String, String> values) {
        return new MutationScope(
                ManifestValueCodec.decode(values.get("id")),
                values.get("kind"),
                Integer.parseInt(values.get("startLine")),
                Integer.parseInt(values.get("endLine")),
                values.get("semanticHash")
        );
    }
}
