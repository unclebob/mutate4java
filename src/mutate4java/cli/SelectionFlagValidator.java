package mutate4java.cli;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.engine.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;
import mutate4java.selection.*;

import mutate4java.selection.*;

import mutate4java.analysis.*;
import mutate4java.coverage.*;
import mutate4java.exec.*;
import mutate4java.manifest.*;

final class SelectionFlagValidator {

    void validate(CliArgumentParseState state) {
        validateScanConflicts(state);
        validateUpdateManifestConflicts(state);
        validateLineConflicts(state);
        validateDifferentialConflicts(state);
    }

    private void validateScanConflicts(CliArgumentParseState state) {
        reject(state.scan && state.sinceLastRun, "--scan may not be combined with --since-last-run");
        reject(state.scan && state.mutateAll, "--scan may not be combined with --mutate-all");
        reject(state.scan && state.updateManifest, "--scan may not be combined with --update-manifest");
    }

    private void validateUpdateManifestConflicts(CliArgumentParseState state) {
        reject(state.updateManifest && state.sinceLastRun, "--update-manifest may not be combined with --since-last-run");
        reject(state.updateManifest && state.mutateAll, "--update-manifest may not be combined with --mutate-all");
        reject(state.updateManifest && !state.lines.isEmpty(), "--update-manifest may not be combined with --lines");
    }

    private void validateLineConflicts(CliArgumentParseState state) {
        reject(!state.lines.isEmpty() && state.sinceLastRun, "--lines may not be combined with --since-last-run");
        reject(!state.lines.isEmpty() && state.mutateAll, "--lines may not be combined with --mutate-all");
    }

    private void validateDifferentialConflicts(CliArgumentParseState state) {
        reject(state.sinceLastRun && state.mutateAll, "--since-last-run may not be combined with --mutate-all");
    }

    private void reject(boolean invalid, String message) {
        if (invalid) {
            throw new IllegalArgumentException(message);
        }
    }
}
