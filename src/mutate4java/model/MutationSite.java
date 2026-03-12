package mutate4java.model;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.cli.*;
import mutate4java.engine.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

import java.nio.file.Path;

public record MutationSite(Path file,
                    int lineNumber,
                    int start,
                    int end,
                    String originalText,
                    String replacementText,
                    String description,
                    String scopeId,
                    String scopeKind,
                    int scopeStartLine,
                    int scopeEndLine) {

    public MutationSite(Path file,
                        int lineNumber,
                        int start,
                        int end,
                        String originalText,
                        String replacementText,
                        String description) {
        this(file, lineNumber, start, end, originalText, replacementText, description,
                "scope:" + lineNumber, "unknown", lineNumber, lineNumber);
    }
}
