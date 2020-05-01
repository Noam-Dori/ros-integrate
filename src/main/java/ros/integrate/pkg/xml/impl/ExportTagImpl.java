package ros.integrate.pkg.xml.impl;

import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.TagTextRange;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExportTagImpl implements ExportTag {
    private static final String MESSAGE_GENERATOR = "message_generator",
            ARCHITECTURE_INDEPENDENT = "architecture_independent",
            DEPRECATED = "deprecated",
            METAPACKAGE = "metapackage",
            BUILD_TYPE = "build_type";

    @NotNull
    private final XmlTag tag;
    @NotNull
    private final ROSPackageXml pkgXml;

    public ExportTagImpl(@NotNull XmlTag tag, @NotNull ROSPackageXml pkgXml) {
        this.tag = tag;
        this.pkgXml = pkgXml;
    }

    @NotNull
    @Override
    public XmlTag getRawTag() {
        return tag;
    }

    @NotNull
    @Override
    public ROSPackageXml getParent() {
        return pkgXml;
    }

    @Nullable
    @Override
    public String getMessageGenerator() {
        return tag.getSubTagText(MESSAGE_GENERATOR);
    }

    @NotNull
    @Override
    public TagTextRange getMessageGeneratorTextRange() {
        return getTagTextRange(MESSAGE_GENERATOR);
    }

    @Override
    public boolean markedArchitectureIndependent() {
        return tag.findFirstSubTag(ARCHITECTURE_INDEPENDENT) != null;
    }

    @NotNull
    @Override
    public TagTextRange getArchitectureIndependentTextRange() {
        return getTagTextRange(ARCHITECTURE_INDEPENDENT);
    }

    @Nullable
    @Override
    public String deprecatedMessage() {
        return tag.getSubTagText(DEPRECATED);
    }

    @Override
    public boolean isMetapackage() {
        return tag.findFirstSubTag(METAPACKAGE) != null;
    }

    @NotNull
    @Override
    public List<BuildType> getBuildTypes() {
        return Arrays.stream(tag.findSubTags(BUILD_TYPE)).map(buildTag ->
                new BuildType(buildTag.getValue().getText(), PackageXmlUtil.getCondition(buildTag)))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<TagTextRange> getBuildTypeTextRanges() {
        List<TagTextRange> ret = Arrays.stream(tag.findSubTags(BUILD_TYPE)).map(TagTextRange::new)
                .collect(Collectors.toList());
        if (ret.isEmpty()) {
            ret.add(new TagTextRange(tag));
        }
        return ret;
    }

    @NotNull
    @Contract("_ -> new")
    private TagTextRange getTagTextRange(@NotNull String tagName) {
        return new TagTextRange(Optional.ofNullable(tag.findFirstSubTag(tagName)).orElse(tag));
    }

    @Override
    public void setBuildType(int id, BuildType newBuildType) {
        XmlTag[] buildTypeTags = tag.findSubTags(BUILD_TYPE);
        if (buildTypeTags.length > id) {
            XmlTag newTag = tag.createChildTag(BUILD_TYPE, null, newBuildType.getType(), false);
            if (newBuildType.getCondition() != null) {
                newTag.setAttribute("condition",newBuildType.getCondition().getText());
            }
            buildTypeTags[id].replace(newTag);
        }
    }

    @Override
    public void removeBuildType(int id) {
        XmlTag[] buildTypeTags = tag.findSubTags(BUILD_TYPE);
        if (buildTypeTags.length > id) {
            buildTypeTags[id].delete();
        }
    }
}
