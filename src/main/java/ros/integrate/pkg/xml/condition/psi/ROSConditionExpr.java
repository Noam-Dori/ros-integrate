package ros.integrate.pkg.xml.condition.psi;

import java.util.List;

public interface ROSConditionExpr extends ROSConditionToken {
    List<ROSConditionToken> getTokens();
}
