package mutate4java;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import com.sun.source.util.Trees;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static javax.tools.JavaCompiler.CompilationTask;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.CLASS_PATH;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class MutationCatalog {

    List<MutationSite> discover(List<Path> files) throws IOException {
        List<MutationSite> sites = new ArrayList<>();
        for (Path file : files) {
            String source = Files.readString(file);
            sites.addAll(astSites(file, source));
        }
        sites.sort(Comparator.comparing(MutationSite::file).thenComparingInt(MutationSite::start));
        return sites;
    }

    private List<MutationSite> astSites(Path file, String source) throws IOException {
        List<MutationSite> sites = new ArrayList<>();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.ROOT, null)) {
            Path outputDir = Files.createTempDirectory("mutate4java-classes");
            fileManager.setLocationFromPaths(CLASS_OUTPUT, List.of(outputDir));
            fileManager.setLocationFromPaths(CLASS_PATH, List.of());
            Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjects(file.toFile());
            CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    List.of("-proc:none", "-implicit:none"),
                    null,
                    javaFiles
            );
            JavacTask javacTask = (JavacTask) task;
            List<CompilationUnitTree> units = new ArrayList<>();
            for (CompilationUnitTree unit : javacTask.parse()) {
                units.add(unit);
            }
            javacTask.analyze();
            Trees trees = Trees.instance(javacTask);
            for (CompilationUnitTree unit : units) {
                new MutationScanner(file, source, unit, trees, sites).scan(unit, null);
            }
        }
        return sites;
    }

    private MutationSite site(Path file, String source, int start, int end, String original, String replacement) {
        return new MutationSite(
                file,
                lineNumber(source, start),
                start,
                end,
                original,
                replacement,
                "replace " + original + " with " + replacement
        );
    }

    private int lineNumber(String source, int index) {
        int line = 1;
        for (int i = 0; i < index; i++) {
            if (source.charAt(i) == '\n') {
                line++;
            }
        }
        return line;
    }

    private final class MutationScanner extends TreePathScanner<Void, Void> {
        private final Path file;
        private final String source;
        private final CompilationUnitTree unit;
        private final Trees trees;
        private final List<MutationSite> sites;

        private MutationScanner(Path file,
                                String source,
                                CompilationUnitTree unit,
                                Trees trees,
                                List<MutationSite> sites) {
            this.file = file;
            this.source = source;
            this.unit = unit;
            this.trees = trees;
            this.sites = sites;
        }

        @Override
        public Void visitLiteral(LiteralTree node, Void unused) {
            if (node.getValue() instanceof Boolean bool) {
                String original = bool ? "true" : "false";
                String replacement = bool ? "false" : "true";
                int start = (int) trees.getSourcePositions().getStartPosition(unit, node);
                if (start >= 0) {
                    sites.add(site(file, source, start, start + original.length(), original, replacement));
                }
            } else if (node.getValue() instanceof Integer value) {
                if (value == 0 || value == 1) {
                    String original = Integer.toString(value);
                    String replacement = value == 0 ? "1" : "0";
                    int start = (int) trees.getSourcePositions().getStartPosition(unit, node);
                    if (start >= 0) {
                        sites.add(site(file, source, start, start + original.length(), original, replacement));
                    }
                }
            }
            return super.visitLiteral(node, unused);
        }

        @Override
        public Void visitBinary(BinaryTree node, Void unused) {
            String original = operatorText(node.getKind());
            String replacement = operatorReplacement(node.getKind());
            if (original != null && replacement != null) {
                MutationSite mutation = binarySite(node, original, replacement);
                if (mutation != null) {
                    sites.add(mutation);
                }
            }
            return super.visitBinary(node, unused);
        }

        @Override
        public Void visitUnary(UnaryTree node, Void unused) {
            MutationSite mutation = unarySite(node);
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
            addNullReplacement(node.getInitializer());
            return super.visitVariable(node, unused);
        }

        @Override
        public Void visitAssignment(AssignmentTree node, Void unused) {
            addNullReplacement(node.getExpression());
            return super.visitAssignment(node, unused);
        }

        private MutationSite binarySite(BinaryTree node, String original, String replacement) {
            long leftEnd = trees.getSourcePositions().getEndPosition(unit, node.getLeftOperand());
            long rightStart = trees.getSourcePositions().getStartPosition(unit, node.getRightOperand());
            if (leftEnd < 0 || rightStart < 0 || leftEnd > rightStart || rightStart > source.length()) {
                return null;
            }
            String between = source.substring((int) leftEnd, (int) rightStart);
            int offset = between.indexOf(original);
            if (offset < 0) {
                return null;
            }
            int start = (int) leftEnd + offset;
            return site(file, source, start, start + original.length(), original, replacement);
        }

        private void addNullReplacement(ExpressionTree expression) {
            if (expression == null) {
                return;
            }
            TreePath path = new TreePath(getCurrentPath(), expression);
            TypeMirror type = trees.getTypeMirror(path);
            if (!isReferenceType(type)) {
                return;
            }
            int start = (int) trees.getSourcePositions().getStartPosition(unit, expression);
            int end = (int) trees.getSourcePositions().getEndPosition(unit, expression);
            if (start < 0 || end < 0 || end <= start) {
                return;
            }
            String original = source.substring(start, end);
            if ("null".equals(original)) {
                return;
            }
            sites.add(site(file, source, start, end, original, "null"));
        }

        private boolean isReferenceType(TypeMirror type) {
            return type != null
                    && type.getKind() != TypeKind.ERROR
                    && type.getKind() != TypeKind.VOID
                    && !type.getKind().isPrimitive();
        }

        private MutationSite unarySite(UnaryTree node) {
            return switch (node.getKind()) {
                case LOGICAL_COMPLEMENT -> removablePrefixSite(node, "!");
                case UNARY_MINUS -> isNumericUnary(node) ? removablePrefixSite(node, "-") : null;
                default -> null;
            };
        }

        private MutationSite removablePrefixSite(UnaryTree node, String operator) {
            int start = (int) trees.getSourcePositions().getStartPosition(unit, node);
            if (start < 0) {
                return null;
            }
            int operatorStart = skipWhitespace(start);
            if (!source.startsWith(operator, operatorStart)) {
                return null;
            }
            return new MutationSite(
                    file,
                    lineNumber(source, operatorStart),
                    operatorStart,
                    operatorStart + operator.length(),
                    operator,
                    "",
                    "replace " + operator + " with removed " + operator
            );
        }

        private int skipWhitespace(int index) {
            int current = index;
            while (current < source.length() && Character.isWhitespace(source.charAt(current))) {
                current++;
            }
            return current;
        }

        private boolean isNumericUnary(UnaryTree node) {
            TypeMirror type = trees.getTypeMirror(getCurrentPath());
            return type != null
                    && type.getKind().isPrimitive()
                    && type.getKind() != TypeKind.BOOLEAN;
        }

        private String operatorText(Tree.Kind kind) {
            return switch (kind) {
                case PLUS -> "+";
                case MINUS -> "-";
                case MULTIPLY -> "*";
                case DIVIDE -> "/";
                case CONDITIONAL_AND -> "&&";
                case CONDITIONAL_OR -> "||";
                case EQUAL_TO -> "==";
                case NOT_EQUAL_TO -> "!=";
                case GREATER_THAN -> ">";
                case GREATER_THAN_EQUAL -> ">=";
                case LESS_THAN -> "<";
                case LESS_THAN_EQUAL -> "<=";
                default -> null;
            };
        }

        private String operatorReplacement(Tree.Kind kind) {
            return switch (kind) {
                case PLUS -> isNumericBinary(getCurrentPath()) ? "-" : null;
                case MINUS -> "+";
                case MULTIPLY -> "/";
                case DIVIDE -> "*";
                case CONDITIONAL_AND -> "||";
                case CONDITIONAL_OR -> "&&";
                case EQUAL_TO -> "!=";
                case NOT_EQUAL_TO -> "==";
                case GREATER_THAN -> ">=";
                case GREATER_THAN_EQUAL -> ">";
                case LESS_THAN -> "<=";
                case LESS_THAN_EQUAL -> "<";
                default -> null;
            };
        }

        private boolean isNumericBinary(TreePath path) {
            TypeMirror type = trees.getTypeMirror(path);
            return type != null && type.getKind().isPrimitive() && type.getKind() != TypeKind.BOOLEAN;
        }
    }
}
