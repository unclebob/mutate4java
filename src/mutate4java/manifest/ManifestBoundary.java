package mutate4java.manifest;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;

final class ManifestBoundary {

    static final String START = "/* mutate4java-manifest\n";
    static final String END = "*/";

    int startIndex(String raw) {
        int start = raw.lastIndexOf(START);
        if (start < 0) {
            return -1;
        }
        String tail = raw.substring(start);
        return tail.trim().endsWith(END) ? start : -1;
    }
}
