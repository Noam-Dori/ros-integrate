package ros.integrate.pkg.xml;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ExportTag {
    @NotNull
    XmlTag getRawTag();

    @NotNull
    ROSPackageXml getParent();

    @Nullable
    String getMessageGenerator();

    @NotNull
    TextRange getMessageGeneratorTextRange();
}
