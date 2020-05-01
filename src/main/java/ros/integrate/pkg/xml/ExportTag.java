package ros.integrate.pkg.xml;

import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.condition.psi.ROSCondition;

import java.util.List;

public interface ExportTag {
    class BuildType {
        @NotNull
        private final String type;

        @Nullable
        private final ROSCondition condition;

        public BuildType(@NotNull String type, @Nullable ROSCondition condition) {
            this.condition = condition;
            this.type = type;
        }

        @Nullable
        public ROSCondition getCondition() {
            return condition;
        }

        @NotNull
        public String getType() {
            return type;
        }
    }

    @NotNull
    XmlTag getRawTag();

    @NotNull
    ROSPackageXml getParent();

    @Nullable
    String getMessageGenerator();

    @NotNull
    TagTextRange getMessageGeneratorTextRange();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean markedArchitectureIndependent();

    @NotNull
    TagTextRange getArchitectureIndependentTextRange();

    @Nullable
    String deprecatedMessage();

    boolean isMetapackage();

    @NotNull
    List<BuildType> getBuildTypes();

    @NotNull
    List<TagTextRange> getBuildTypeTextRanges();

    void setBuildType(int id, BuildType newBuildType);
}
