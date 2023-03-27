package ros.integrate.pkg.xml.condition.psi;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * the most general form of ROS conditions. This can represent entire ROS conditions, sub-expressions, and single
 * variables/literals
 * @author Noam Dori
 */
public interface ROSConditionExpr extends ROSConditionToken {
    /**
     * A variant of this class that runs lazy evaluations.
     */
    class LazyExpr {
        @NotNull
        private final ROSConditionExpr expr;
        String result = null;

        private LazyExpr(@NotNull ROSConditionToken expr) {
            this.expr = (ROSConditionExpr) expr;
        }

        String evaluate() {
            if (result == null) {
                result = expr.evaluate();
            }
            return result;
        }
    }

    String TRUE = "\1", FALSE = ""; // while here we substitute a string values for booleans, python doesn't do that.
    Map<String, Predicate<Integer>> comparisons =
            Map.of("!=", compare -> compare != 0,
                    "<", compare -> compare < 0,
                    "<=", compare -> compare <= 0,
                    "==", compare -> compare == 0,
                    ">=", compare -> compare >= 0,
                    ">", compare -> compare > 0);

    /**
     * @return get the list of actual tokens that make up this expression. This does not include whitespaces.
     */
    List<ROSConditionToken> getTokens();

    /**
     * assuming the expression is valid ({@link ROSConditionExpr#checkValid()} returns <code>true</code>),
     * this method evaluates the expression as if it is a python expression,
     * with the special rule that strings starting with $ are substituted with their environment variable value.
     * This method is recursive, but uses a lot of lazy evaluations to make the calculation as efficient as possible.
     * @return {@link ROSConditionExpr#TRUE} if the expression evaluated to a boolean and returned <code>true</code>,
     *         {@link ROSConditionExpr#FALSE} if the expression evaluated to a boolean and returned <code>false</code>,
     *         or a proper string value if the expression evaluated to a string.
     */
    @NotNull
    default String evaluate() {
        String returnValue;
        LazyExpr lastExpr;
        boolean ignore = false;
        Iterator<ROSConditionToken> iter = getTokens().iterator();

        lastExpr = new LazyExpr(iter.next());
        returnValue = lastExpr.evaluate();
        while (iter.hasNext()) {
            String operation = iter.next().getText();
            LazyExpr nextExpr = new LazyExpr(iter.next());
            switch (operation) {
                case "and" -> {
                    if (returnValue.isEmpty()) {
                        ignore = true;
                    } else {
                        returnValue = nextExpr.evaluate();
                        lastExpr = nextExpr;
                    }
                }
                case "or" -> {
                    if (returnValue.isEmpty()) {
                        ignore = false;
                        returnValue = nextExpr.evaluate();
                        lastExpr = nextExpr;
                    } else {
                        return returnValue; // yes a return value of all things. It's a crazy wORld out there.
                    }
                }
                default -> { // comparisons: ==,!=,<=,<,>,>=
                    if (!ignore) {
                        returnValue = comparisons.get(operation)
                                .test(lastExpr.evaluate().compareTo(nextExpr.evaluate())) ? TRUE : FALSE;
                        ignore = returnValue.isEmpty();
                        lastExpr = nextExpr;
                    }
                }
            }
        }
        return returnValue;
    }

    /**
     * checks whether this expression is a legal ROS condition according to REP 149.
     * Even if it is not the entire condition, it still follows the rules of complete conditions.
     * @return true if:
     * <ul>
     *     <li>all sub-expressions are valid (the recursion ends with the verbs, check
     *     {@link ROSConditionItem#checkValid()} for details)</li>
     *     <li>all sub-expressions are delimited by logic tokens</li>
     *     <li>there is an odd number of tokens </li>
     * </ul>
     */
    default boolean checkValid() {
        boolean operator = false;
        for (ROSConditionToken token : getTokens()) {
            if (operator ? !(token instanceof ROSConditionLogic) :
                    !(token instanceof ROSConditionExpr) || !((ROSConditionExpr) token).checkValid()) {
                return false;
            }
            operator = !operator;
        }
        return operator; // checks last element is not an operator.
    }
}
