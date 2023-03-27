package ros.integrate.pkg.xml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * an enum of all dependency types.
 */
public enum DependencyType {
    BUILD("build_depend", 3, 1),
    BUILD_EXPORT("build_export_depend", 5, 2),
    BUILDTOOL("buildtool_depend", 0, 1),
    BUILDTOOL_EXPORT("buildtool_export_depend", 1, 2),
    EXEC("exec_depend", 6, 2),
    DOC("doc_depend", 7, 2),
    TEST("test_depend", 8, 1),
    CONFLICT("conflict", 9, 1),
    REPLACE("replace", 10, 1),
    DEFAULT("depend", 2, 2, BUILD, BUILD_EXPORT, EXEC),
    RUN("run_depend", 4, 1, 1, BUILD_EXPORT, EXEC);

    private final String tagName;
    private final int sinceFormat, untilFormat, ordinal;
    private DependencyType[] split;

    /**
     * construct a new dependency type
     * @param tagName the name of the tag that uses it
     * @param ordinal the sorting order of this type in a package.xml file
     * @param sinceFormat the format version this dependency was added
     * @param untilFormat the last format version this dependency was still valid. use -1 if it has no "last version"
     * @param split if this is a composite dependency type, this list describes the types this dependency covers.
     *              if it is a base type, set this to itself ("this")
     */
    @Contract(pure = true)
    DependencyType(String tagName, int ordinal, int sinceFormat, int untilFormat, DependencyType... split) {
        this.tagName = tagName;
        this.ordinal = ordinal;
        this.sinceFormat = sinceFormat;
        this.untilFormat = untilFormat;
        this.split = split;
    }

    /**
     * construct a new dependency type
     * @param tagName the name of the tag that uses it
     * @param ordinal the sorting order of this type in a package.xml file
     * @param sinceFormat the format version this dependency was added
     * @param split if this is a composite dependency type, this list describes the types this dependency covers.
     *              if it is a base type, set this to itself ("this")
     */
    @SuppressWarnings("SameParameterValue")
    @Contract(pure = true)
    DependencyType(String tagName, int ordinal, int sinceFormat, DependencyType... split) {
        this(tagName, ordinal, sinceFormat, -1, split);
    }

    /**
     * construct a new dependency type
     * @param tagName the name of the tag that uses it
     * @param ordinal the sorting order of this type in a package.xml file
     * @param sinceFormat the format version this dependency was added
     */
    @Contract(pure = true)
    DependencyType(String tagName, int ordinal, int sinceFormat) {
        this(tagName, ordinal, sinceFormat, -1);
        split = new DependencyType[]{this};
    }

    /**
     * @return the name of the tag that uses this type
     */
    @Contract(pure = true)
    public String getTagName() {
        return tagName;
    }

    /**
     * @return if this is a composite dependency type, this list describes the types this dependency covers.
     *         if it is a base type, it returns a list containing itself.
     */
    @Contract(pure = true)
    @NotNull
    public DependencyType[] getCoveredDependencies() {
        return split;
    }

    /**
     * get the dependency tags that cover this base dependency type.
     * @param format the format of the respective package.xml file.
     * @return an array of all dependency tags that cover this base dependency type. For base tags,
     * size will be at least 1. For composite types this will be an empty list.
     */
    @NotNull
    public DependencyType[] getCoveringTags(int format) {
        return Arrays.stream(DependencyType.values())
                .filter(dep -> Arrays.asList(dep.split).contains(this))
                .filter(dep -> dep.relevant(format))
                .toArray(DependencyType[]::new);
    }

    /**
     * checks if this tag is valid in a specific format
     * @param format the format to check against. putting {@literal -1} will always return true.
     * @return true if the dependency type is valid in the given format or if the format is -1, false otherwise
     */
    @Contract(pure = true)
    public boolean relevant(int format) {
        return format == -1 || format >= sinceFormat && (untilFormat == -1 || format <= untilFormat);
    }

    /**
     * comparison between dependency types. Used for sorting them in a package.xml file
     * @param dep1 the LHS
     * @param dep2 the RHS
     * @return the comparison integer between the two types (see {@link Integer#compare(int, int)} for details)
     */
    @Contract(pure = true)
    public static int compare(@NotNull DependencyType dep1, @NotNull DependencyType dep2) {
        return Integer.compare(dep1.ordinal, dep2.ordinal);
    }
}