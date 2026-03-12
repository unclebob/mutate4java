package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class AstMutationScanner extends TreePathScanner<Void, Void> {
    private final List<MutationSite> sites;
    private final AstScopeTracker scopeTracker;
    private final AstMutationSiteFactory siteFactory;

    AstMutationScanner(Path file, String source, CompilationUnitTree unit, Trees trees, List<MutationSite> sites) {
        this.sites = sites;
        LineNumberTable lineNumbers = new LineNumberTable(source);
        this.scopeTracker = new AstScopeTracker(file, source, unit, trees, lineNumbers);
        this.siteFactory = new AstMutationSiteFactory(file, source, unit, trees, lineNumbers, scopeTracker);
    }

    List<MutationScope> scopes() {
        return scopeTracker.scopes();
    }

    @Override
    public Void visitClass(ClassTree node, Void unused) {
        scopeTracker.enterClass(node);
        try {
            return super.visitClass(node, unused);
        } finally {
            scopeTracker.exitClass();
        }
    }

    @Override
    public Void visitMethod(com.sun.source.tree.MethodTree node, Void unused) {
        scopeTracker.visitMethod(node);
        return super.visitMethod(node, unused);
    }

    @Override
    public Void visitLiteral(LiteralTree node, Void unused) {
        MutationSite mutation = siteFactory.literal(getCurrentPath(), node);
        if (mutation != null) {
            sites.add(mutation);
        }
        return super.visitLiteral(node, unused);
    }

    @Override
    public Void visitBinary(BinaryTree node, Void unused) {
        MutationSite mutation = siteFactory.binary(getCurrentPath(), node);
        if (mutation != null) {
            sites.add(mutation);
        }
        return super.visitBinary(node, unused);
    }

    @Override
    public Void visitUnary(UnaryTree node, Void unused) {
        MutationSite mutation = siteFactory.unary(getCurrentPath(), node);
        if (mutation != null) {
            sites.add(mutation);
        }
        return super.visitUnary(node, unused);
    }

    @Override
    public Void visitReturn(ReturnTree node, Void unused) {
        addNullReplacement(node.getExpression());
        return super.visitReturn(node, unused);
    }

    @Override
    public Void visitVariable(VariableTree node, Void unused) {
        scopeTracker.visitVariable(getCurrentPath(), node);
        addNullReplacement(node.getInitializer());
        return super.visitVariable(node, unused);
    }

    @Override
    public Void visitAssignment(AssignmentTree node, Void unused) {
        addNullReplacement(node.getExpression());
        return super.visitAssignment(node, unused);
    }

    private void addNullReplacement(ExpressionTree expression) {
        MutationSite mutation = siteFactory.nullReplacement(getCurrentPath(), expression);
        if (mutation != null) {
            sites.add(mutation);
        }
    }
}
