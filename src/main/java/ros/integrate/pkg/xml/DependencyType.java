package ros.integrate.pkg.xml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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

    @Contract(pure = true)
    DependencyType(String tagName, int ordinal, int sinceFormat, int untilFormat, DependencyType... split) {
        this.tagName = tagName;
        this.ordinal = ordinal;
        this.sinceFormat = sinceFormat;
        this.untilFormat = untilFormat;
        this.split = split;
    }

    @SuppressWarnings("SameParameterValue")
    @Contract(pure = true)
    DependencyType(String tagName, int ordinal, int sinceFormat, DependencyType... split) {
        this(tagName, ordinal, sinceFormat, -1, split);
    }

    @Contract(pure = true)
    DependencyType(String tagName, int ordinal, int sinceFormat) {
        this(tagName, ordinal, sinceFormat, -1);
        split = new DependencyType[]{this};
    }

    @Contract(pure = true)
    public String getTagName() {
        return tagName;
    }

    @Contract(pure = true)
    @NotNull
    public DependencyType[] getCoveredDependencies() {
        return split;
    }

    @Contract(pure = true)
    public boolean relevant(int format) {
        return format == -1 || format >= sinceFormat && (untilFormat == -1 || format <= untilFormat);
    }

    @Contract(pure = true)
    public static int compare(@NotNull DependencyType dep1, @NotNull DependencyType dep2) {
        return Integer.compare(dep1.ordinal, dep2.ordinal);
    }
}