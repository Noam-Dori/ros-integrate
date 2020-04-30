package ros.integrate.pkg.xml.condition.psi;

import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
            ContainerUtil.newHashMap(
                    Pair.create("!=", compare -> compare != 0),
                    Pair.create("<", compare -> compare < 0),
                    Pair.create("<=", compare -> compare <= 0),
                    Pair.create("==", compare -> compare == 0),
                    Pair.create(">=", compare -> compare >= 0),
                    Pair.create(">", compare -> compare > 0)
            );

    List<ROSConditionToken> getTokens();

    /**
     * assuming the expression is valid ({@link ROSConditionExpr#checkValid()} returns <code>true</code>)
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
                case "and": {
                    if (returnValue.isEmpty()) {
                        ignore = true;
                    } else {
                        returnValue = nextExpr.evaluate();
                        lastExpr = nextExpr;
                    }
                    break;
                }
                case "or": {
                    if (returnValue.isEmpty()) {
                        ignore = false;
                        returnValue = nextExpr.evaluate();
                        lastExpr = nextExpr;
                    } else {
                        return returnValue; // yes a return value of all things. It's a crazy wORld out there.
                    }
                    break;
                }
                default: { // comparisons: ==,!=,<=,<,>,>=
                    if (!ignore) {
                        returnValue = comparisons.get(operation)
                                .test(lastExpr.evaluate().compareTo(nextExpr.evaluate())) ? TRUE : FALSE;
                        ignore = returnValue.isEmpty();
                        lastExpr = nextExpr;
                    }
                    break;
                }
            }
        }
        return returnValue;
    }

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
