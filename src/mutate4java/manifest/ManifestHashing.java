package mutate4java.manifest;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;

final class ManifestHashing {

    String hashScopes(List<MutationScope> scopes) {
        StringBuilder out = new StringBuilder();
        scopes.stream().sorted(Comparator.comparing(MutationScope::id))
                .forEach(scope -> out.append(scope.id()).append('|').append(scope.semanticHash()).append('\n'));
        return hash(out.toString());
    }

    String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte value : bytes) {
                hex.append(String.format("%02x", value));
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash manifest content", ex);
        }
    }
}
