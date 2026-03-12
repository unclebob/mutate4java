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

public record ScopeRef(String id, String kind, int startLine, int endLine) {

    public static ScopeRef from(MutationScope scope) {
        return new ScopeRef(scope.id(), scope.kind(), scope.startLine(), scope.endLine());
    }
}
