package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class AstScopeTracker {

    private final Path file;
    private final String source;
    private final com.sun.source.tree.CompilationUnitTree unit;
    private final Trees trees;
    private final LineNumberTable lineNumbers;
    private final MutationScopeFactory scopeFactory;
    private final List<MutationScope> scopes = new ArrayList<>();
    private final Deque<String> classNames = new ArrayDeque<>();

    AstScopeTracker(Path file, String source, com.sun.source.tree.CompilationUnitTree unit, Trees trees, LineNumberTable lineNumbers) {
        this.file = file;
        this.source = source;
        this.unit = unit;
        this.trees = trees;
        this.lineNumbers = lineNumbers;
        this.scopeFactory = new MutationScopeFactory(source, unit, trees, lineNumbers);
    }

    List<MutationScope> scopes() {
        return List.copyOf(scopes);
    }

    void enterClass(ClassTree node) {
        classNames.push(node.getSimpleName().toString());
        addScope(classScope(node));
    }

    void exitClass() {
        classNames.pop();
    }

    void visitMethod(MethodTree node) {
        addScope(methodScope(node));
    }

    void visitVariable(TreePath path, VariableTree node) {
        if (isField(path)) {
            addScope(fieldScope(node));
        }
    }

    ScopeRef currentScope(TreePath path) {
        while (path != null) {
            Tree leaf = path.getLeaf();
            if (leaf instanceof MethodTree method) {
                MutationScope scope = methodScope(method);
                addScope(scope);
                return ScopeRef.from(scope);
            }
            if (leaf instanceof VariableTree variable && isField(path)) {
                MutationScope scope = fieldScope(variable);
                addScope(scope);
                return ScopeRef.from(scope);
            }
            if (leaf instanceof ClassTree type) {
                MutationScope scope = classScope(type);
                addScope(scope);
                return ScopeRef.from(scope);
            }
            path = path.getParentPath();
        }
        return new ScopeRef("file:" + file.getFileName(), "file", 1, lineNumbers.lineNumber(source.length()));
    }

    private void addScope(MutationScope scope) {
        if (scopes.stream().noneMatch(existing -> existing.id().equals(scope.id()))) {
            scopes.add(scope);
        }
    }

    private MutationScope classScope(ClassTree node) {
        return scope(id("class", node.getSimpleName().toString(), node), "class", node);
    }

    private MutationScope methodScope(MethodTree node) {
        String name = node.getName().contentEquals("<init>") ? "ctor" : node.getName().toString();
        return scope(id("method", name + "(" + node.getParameters().size() + ")", node), "method", node);
    }

    private MutationScope fieldScope(VariableTree node) {
        return scope(id("field", node.getName().toString(), node), "field", node);
    }

    private MutationScope scope(String id, String kind, Tree node) {
        int start = (int) trees.getSourcePositions().getStartPosition(unit, node);
        return scopeFactory.create(id, kind, node);
    }

    private String id(String kind, String detail, Tree node) {
        int start = (int) trees.getSourcePositions().getStartPosition(unit, node);
        List<String> names = new ArrayList<>(classNames);
        java.util.Collections.reverse(names);
        String prefix = String.join(".", names);
        if (!prefix.isBlank()) {
            prefix += "#";
        }
        return kind + ":" + prefix + detail + ":" + lineNumbers.lineNumber(Math.max(start, 0));
    }

    private boolean isField(TreePath path) {
        TreePath parent = path.getParentPath();
        return parent != null && parent.getLeaf() instanceof ClassTree;
    }
}
