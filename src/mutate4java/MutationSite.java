package mutate4java;

import java.nio.file.Path;

record MutationSite(Path file,
                    int lineNumber,
                    int start,
                    int end,
                    String originalText,
                    String replacementText,
                    String description) {
}
