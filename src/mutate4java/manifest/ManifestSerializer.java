package mutate4java.manifest;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

final class ManifestSerializer {

    String serialize(DifferentialManifest manifest, String start, String end) {
        StringBuilder out = new StringBuilder();
        out.append(start);
        out.append("version=").append(manifest.version()).append('\n');
        out.append("moduleHash=").append(manifest.moduleHash()).append('\n');
        for (int i = 0; i < manifest.scopes().size(); i++) {
            appendScope(out, i, manifest.scopes().get(i));
        }
        out.append(end).append('\n');
        return out.toString();
    }

    private void appendScope(StringBuilder out, int index, MutationScope scope) {
        out.append("scope.").append(index).append(".id=").append(ManifestValueCodec.encode(scope.id())).append('\n');
        out.append("scope.").append(index).append(".kind=").append(scope.kind()).append('\n');
        out.append("scope.").append(index).append(".startLine=").append(scope.startLine()).append('\n');
        out.append("scope.").append(index).append(".endLine=").append(scope.endLine()).append('\n');
        out.append("scope.").append(index).append(".semanticHash=").append(scope.semanticHash()).append('\n');
    }
}
