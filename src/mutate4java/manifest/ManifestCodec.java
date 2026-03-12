package mutate4java.manifest;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

final class ManifestCodec {

    private final ManifestParser parser = new ManifestParser();
    private final ManifestSerializer serializer = new ManifestSerializer();

    DifferentialManifest parse(String body) {
        return parser.parse(body);
    }

    String serialize(DifferentialManifest manifest, String start, String end) {
        return serializer.serialize(manifest, start, end);
    }
}
