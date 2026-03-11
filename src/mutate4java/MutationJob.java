package mutate4java;

import java.nio.file.Path;

record MutationJob(MutationSite site,
                   Path sourceRelativePath,
                   long timeoutMillis,
                   int order,
                   int totalJobs) {
}
