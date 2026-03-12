package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class TreeTypePredicates {

    private final Trees trees;

    TreeTypePredicates(Trees trees) {
        this.trees = trees;
    }

    boolean isNumeric(TreePath path) {
        TypeMirror type = trees.getTypeMirror(path);
        return type != null && type.getKind().isPrimitive() && type.getKind() != TypeKind.BOOLEAN;
    }

    boolean isReference(TreePath parentPath, ExpressionTree expression) {
        TypeMirror type = trees.getTypeMirror(new TreePath(parentPath, expression));
        return type != null
                && type.getKind() != TypeKind.ERROR
                && type.getKind() != TypeKind.VOID
                && !type.getKind().isPrimitive();
    }
}
