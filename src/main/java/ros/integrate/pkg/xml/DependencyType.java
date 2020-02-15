package ros.integrate.pkg.xml;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * an enum of all dependency types.
 */
public enum DependencyType {
    BUILD("build_depend",1),
    BUILD_EXPORT("build_export_depend",2),
    BUILDTOOL("buildtool_depend",1),
    BUILDTOOL_EXPORT("buildtool_export_depend",2),
    EXEC("exec_depend",2),
    DOC("doc_depend",2),
    TEST("test_depend",1),
    DEFAULT("depend",2, BUILD, BUILD_EXPORT, EXEC),
    RUN("run_depend",1,1, BUILD_EXPORT, EXEC);

    private final String tagName;
    private final int sinceFormat, untilFormat;
    private DependencyType[] split;

    @Contract(pure = true)
    DependencyType(String tagName, int sinceFormat, int untilFormat, DependencyType... split) {
        this.tagName = tagName;
        this.sinceFormat = sinceFormat;
        this.untilFormat = untilFormat;
        this.split = split;
    }

    @Contract(pure = true)
    DependencyType(String tagName, int sinceFormat, DependencyType... split) {
        this(tagName, sinceFormat, -1, split);
    }

    @Contract(pure = true)
    DependencyType(String tagName, int sinceFormat) {
        this(tagName, sinceFormat, -1);
        split = new DependencyType[]{this};
    }

    @Contract(pure = true)
    public String getTagName() {
        return tagName;
    }

    public List<DependencyType> getValidTags(int format) {
        return Arrays.stream(DependencyType.values())
                .filter(dep -> Arrays.asList(dep.split).contains(this))
                .filter(dep -> dep.relevant(format))
                .collect(Collectors.toList());
    }

    @Contract(pure = true)
    public boolean relevant(int format) {
        return format == -1 || format >= sinceFormat && (untilFormat == -1 || format <= untilFormat);
    }
}