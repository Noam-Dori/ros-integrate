package ros.integrate.pkg.xml.condition.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.lang.ROSConditionLanguage;

public class ROSConditionElementType extends IElementType {
    public ROSConditionElementType(@NotNull @NonNls String debugName) {
        super(debugName, ROSConditionLanguage.INSTANCE);
    }
}
