package ros.integrate.pkg.xml;

import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.condition.psi.ROSCondition;

import java.util.List;

/**
 * a facade class that acts as the logical representation of the export tag.
 * It has methods that give you the data it contains.
 * @author Noam Dori
 */
public interface ExportTag {
    /**
     * a data representation of the build_type tag
     */
    class BuildType implements ROSCondition.Conditioned {
        @NotNull
        private final String type;

        @Nullable
        private final ROSCondition condition;

        /**
         * construct a new logical build type tag
         * @param type the build type itself
         * @param condition the condition over which the build type is used.
         */
        public BuildType(@NotNull String type, @Nullable ROSCondition condition) {
            this.condition = condition;
            this.type = type;
        }

        @Nullable
        public ROSCondition getCondition() {
            return condition;
        }

        /**
         * @return the build system of the package
         */
        @NotNull
        public String getType() {
            return type;
        }
    }

    /**
     * @return the raw XML tag this wrapper manages
     */
    @NotNull
    XmlTag getRawTag();

    /**
     * @return the package.xml file this export tag belongs to
     */
    @NotNull
    ROSPackageXml getParent();

    /**
     * gets the language the message generator generates.
     * @return null if the package is not declared as a message generator,
     * otherwise the name of the language this package generates messages for
     */
    @Nullable
    String getMessageGenerator();

    /**
     * @return the text range of the message_generator tag
     */
    @NotNull
    TagTextRange getMessageGeneratorTextRange();

    /**
     * @return true if this package has an architecture_independent tag, false otherwise.
     * If true, then this package does not depend on the architecture of the computer for compilation.
     * python packages for example are architecture independent.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean markedArchitectureIndependent();

    /**
     * @return the text range of the architecture_independent tag
     */
    @NotNull
    TagTextRange getArchitectureIndependentTextRange();

    /**
     * @return null if the package is not deprecated, otherwise the text value of the deprecated tag
     */
    @Nullable
    String deprecatedMessage();

    /**
     * @return true if this package was marked as a metapackage by a metapackage tag, false otherwise
     */
    boolean isMetapackage();

    /**
     * @return a list of all build systems this package uses. There can be multiple build systems thanks to conditions
     */
    @NotNull
    List<BuildType> getBuildTypes();

    /**
     * @return a list the text ranges of all build_type tags
     */
    @NotNull
    List<TagTextRange> getBuildTypeTextRanges();

    /**
     * changes the data of a build_type tag
     * @param id the identifier of the resource - its index in the list of all build types in the export tag.
     * @param newBuildType the new build type data to put
     */
    void setBuildType(int id, BuildType newBuildType);

    /**
     * deletes a build_type tag
     * @param id the identifier of the resource - its index in the list of all build types in the export tag.
     */
    void removeBuildType(int id);
}
