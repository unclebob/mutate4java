package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;

final class MutationScopeFactory {

    private final String source;
    private final com.sun.source.tree.CompilationUnitTree unit;
    private final Trees trees;
    private final LineNumberTable lineNumbers;
    private final ManifestSupport manifestSupport = new ManifestSupport();

    MutationScopeFactory(String source,
                         com.sun.source.tree.CompilationUnitTree unit,
                         Trees trees,
                         LineNumberTable lineNumbers) {
        this.source = source;
        this.unit = unit;
        this.trees = trees;
        this.lineNumbers = lineNumbers;
    }

    MutationScope create(String id, String kind, Tree node) {
        int start = (int) trees.getSourcePositions().getStartPosition(unit, node);
        int end = (int) trees.getSourcePositions().getEndPosition(unit, node);
        if (start < 0 || end <= start) {
            start = 0;
            end = source.length();
        }
        return new MutationScope(id, kind, lineNumbers.lineNumber(start),
                lineNumbers.lineNumber(Math.max(start, end - 1)),
                manifestSupport.hash(source.substring(start, end)));
    }
}
