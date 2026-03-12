package mutate4java.analysis;

import mutate4java.project.*;
import mutate4java.report.*;

import mutate4java.model.*;

import mutate4java.*;
import mutate4java.manifest.*;

import com.sun.source.tree.Tree;

record BinaryMutationOperator(String original, String replacement, boolean numericOnly) {

    static BinaryMutationOperator forKind(Tree.Kind kind) {
        return switch (kind) {
            case PLUS -> new BinaryMutationOperator("+", "-", true);
            case MINUS -> new BinaryMutationOperator("-", "+", false);
            case MULTIPLY -> new BinaryMutationOperator("*", "/", false);
            case DIVIDE -> new BinaryMutationOperator("/", "*", false);
            case CONDITIONAL_AND -> new BinaryMutationOperator("&&", "||", false);
            case CONDITIONAL_OR -> new BinaryMutationOperator("||", "&&", false);
            case EQUAL_TO -> new BinaryMutationOperator("==", "!=", false);
            case NOT_EQUAL_TO -> new BinaryMutationOperator("!=", "==", false);
            case GREATER_THAN -> new BinaryMutationOperator(">", ">=", false);
            case GREATER_THAN_EQUAL -> new BinaryMutationOperator(">=", ">", false);
            case LESS_THAN -> new BinaryMutationOperator("<", "<=", false);
            case LESS_THAN_EQUAL -> new BinaryMutationOperator("<=", "<", false);
            default -> null;
        };
    }
}
