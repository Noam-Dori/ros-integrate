package ros.integrate.pkg.xml.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.xml.ExportTag;
import ros.integrate.pkg.xml.ROSPackageXml;

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
    @Contract(" -> new")
    private TextRange getRootTextRange() {
        return new TextRange(tag.getTextOffset() + 1, tag.getTextOffset() + 1 + tag.getName().length());
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
    public TextRange getMessageGeneratorTextRange() {
        XmlTag found = tag.findFirstSubTag(MESSAGE_GENERATOR);
        if (found == null) {
            return getRootTextRange();
        }
        if (found.getValue().getText().isEmpty()) {
            return new TextRange(found.getTextOffset() + 1, found.getTextOffset() + 1 + MESSAGE_GENERATOR.length());
        }
        return found.getValue().getTextRange();
    }

    @Override
    public boolean markedArchitectureIndependent() {
        return tag.findFirstSubTag(ARCHITECTURE_INDEPENDENT) != null;
    }

    @NotNull
    @Override
    public TextRange getArchitectureIndependentTextRange() {
        XmlTag found = tag.findFirstSubTag(ARCHITECTURE_INDEPENDENT);
        if (found == null) {
            return getRootTextRange();
        }
        if (found.getValue().getText().isEmpty()) {
            return new TextRange(found.getTextOffset() + 1, found.getTextOffset() + 1 +
                    ARCHITECTURE_INDEPENDENT.length());
        }
        return found.getValue().getTextRange();
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

    @Nullable
    @Override
    public String getBuildType() {
        return tag.getSubTagText(BUILD_TYPE);
    }
}
