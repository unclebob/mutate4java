package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import java.nio.file.Path;

final class AstMutationSiteFactory {

    private final Path file;
    private final String source;
    private final com.sun.source.tree.CompilationUnitTree unit;
    private final Trees trees;
    private final LineNumberTable lineNumbers;
    private final TreeTypePredicates typePredicates;
    private final AstScopeTracker scopeTracker;

    AstMutationSiteFactory(Path file,
                           String source,
                           com.sun.source.tree.CompilationUnitTree unit,
                           Trees trees,
                           LineNumberTable lineNumbers,
                           AstScopeTracker scopeTracker) {
        this.file = file;
        this.source = source;
        this.unit = unit;
        this.trees = trees;
        this.lineNumbers = lineNumbers;
        this.typePredicates = new TreeTypePredicates(trees);
        this.scopeTracker = scopeTracker;
    }

    MutationSite literal(TreePath path, LiteralTree node) {
        if (node.getValue() instanceof Boolean bool) {
            return literalSite(path, node, bool ? "true" : "false", bool ? "false" : "true");
        }
        if (node.getValue() instanceof Integer value && (value == 0 || value == 1)) {
            return literalSite(path, node, Integer.toString(value), value == 0 ? "1" : "0");
        }
        return null;
    }

    MutationSite binary(TreePath path, BinaryTree node) {
        BinaryMutationOperator operator = BinaryMutationOperator.forKind(node.getKind());
        if (operator == null || (operator.numericOnly() && !typePredicates.isNumeric(path))) {
            return null;
        }
        long leftEnd = trees.getSourcePositions().getEndPosition(unit, node.getLeftOperand());
        long rightStart = trees.getSourcePositions().getStartPosition(unit, node.getRightOperand());
        if (leftEnd < 0 || rightStart < 0 || leftEnd > rightStart || rightStart > source.length()) {
            return null;
        }
        String between = source.substring((int) leftEnd, (int) rightStart);
        int offset = between.indexOf(operator.original());
        if (offset < 0) {
            return null;
        }
        int start = (int) leftEnd + offset;
        return site(path, start, start + operator.original().length(), operator.original(), operator.replacement());
    }

    MutationSite unary(TreePath path, UnaryTree node) {
        return switch (node.getKind()) {
            case LOGICAL_COMPLEMENT -> removablePrefix(path, node, "!");
            case UNARY_MINUS -> typePredicates.isNumeric(path) ? removablePrefix(path, node, "-") : null;
            default -> null;
        };
    }

    MutationSite nullReplacement(TreePath path, ExpressionTree expression) {
        if (expression == null || !typePredicates.isReference(path, expression)) {
            return null;
        }
        int start = (int) trees.getSourcePositions().getStartPosition(unit, expression);
        int end = (int) trees.getSourcePositions().getEndPosition(unit, expression);
        if (start < 0 || end <= start) {
            return null;
        }
        String original = source.substring(start, end);
        return "null".equals(original) ? null : site(path, start, end, original, "null");
    }

    private MutationSite literalSite(TreePath path, LiteralTree node, String original, String replacement) {
        int start = (int) trees.getSourcePositions().getStartPosition(unit, node);
        return start < 0 ? null : site(path, start, start + original.length(), original, replacement);
    }

    private MutationSite removablePrefix(TreePath path, UnaryTree node, String operator) {
        int start = (int) trees.getSourcePositions().getStartPosition(unit, node);
        if (start < 0) {
            return null;
        }
        int operatorStart = lineNumbers.skipWhitespace(start);
        if (!source.startsWith(operator, operatorStart)) {
            return null;
        }
        return buildSite(path, operatorStart, operatorStart + operator.length(),
                operator, "", "replace " + operator + " with removed " + operator);
    }

    private MutationSite site(TreePath path, int start, int end, String original, String replacement) {
        return buildSite(path, start, end, original, replacement, "replace " + original + " with " + replacement);
    }

    private MutationSite buildSite(TreePath path, int start, int end, String original, String replacement, String description) {
        ScopeRef scope = scopeTracker.currentScope(path);
        return new MutationSite(file, lineNumbers.lineNumber(start), start, end, original, replacement,
                description, scope.id(), scope.kind(), scope.startLine(), scope.endLine());
    }
}
