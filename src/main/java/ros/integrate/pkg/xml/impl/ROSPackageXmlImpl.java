package ros.integrate.pkg.xml.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.XmlElementFactory;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.ROSPackageXml;

import java.util.*;
import java.util.stream.Collectors;

public class ROSPackageXmlImpl implements ROSPackageXml {
    private enum Component {
        NAME("name"),
        VERSION("version"),
        DESCRIPTION("description"),
        MAINTAINER("maintainer"),
        LICENCE("license"),
        AUTHOR("author"),
        URL("url");
        String lookup;

        @Contract(pure = true)
        String get() {
            return lookup;
        }

        @Contract(pure = true)
        Component(String lookup) {
            this.lookup = lookup;
        }
    }


    private XmlFile file;
    private ROSPackage pkg;

    @Contract(pure = true)
    public ROSPackageXmlImpl(@NotNull XmlFile xmlToWrap, @NotNull ROSPackage pkg) {
        file = xmlToWrap;
        this.pkg = pkg;
    }

    @NotNull
    @Override
    public XmlFile getRawXml() {
        return file;
    }

    @Override
    public void setRawXml(@NotNull XmlFile newXml) {
        file = newXml;
    }

    @Override
    public int getFormat() {
        if (file.getRootTag() == null) {
            return 0;
        }
        XmlAttribute format = file.getRootTag().getAttribute("format");
        if (format == null || format.getValue() == null) {
            return 1;
        }
        try {
            return Integer.parseInt(format.getValue());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @NotNull
    @Override
    public TextRange getFormatTextRange() {
        if (file.getRootTag() == null) {
            return file.getTextRange();
        }
        XmlAttribute format = file.getRootTag().getAttribute("format");
        if (format == null || format.getValueElement() == null) {
            return getRootTextRange();
        }
        return format.getValueElement().getValueTextRange();
    }

    @Override
    public ROSPackage getPackage() {
        return pkg;
    }

    @Override
    public String getPkgName() {
        return getTextComponent(Component.NAME);
    }

    @Override
    public TextRange getRootTextRange() {
        if (file.getRootTag() == null) {
            return file.getTextRange();
        }
        return new TextRange(file.getRootTag().getTextOffset() + 1, file.getRootTag().getTextOffset() + 1 +
                file.getRootTag().getName().length());
    }

    @Override
    public TextRange getNameTextRange() {
        return getComponentTextRange(Component.NAME);
    }

    @Override
    public void setNewFormat() {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        file.getRootTag().setAttribute("format", "2");
    }

    @Override
    public void setPkgName(String pkgName) {
        setComponent(Component.NAME, pkgName);
    }

    @Override
    public String getVersion() {
        return getTextComponent(Component.VERSION);
    }

    @NotNull
    @Override
    public TextRange getVersionTextRange() {
        return getComponentTextRange(Component.VERSION);
    }

    @Override
    public void setVersion(String newVersion) {
        setComponent(Component.VERSION, newVersion);
    }

    @Override
    public String getDescription() {
        return getTextComponent(Component.DESCRIPTION);
    }

    @Override
    public void setDescription(String newDescription) {
        setComponent(Component.DESCRIPTION, newDescription);
    }

    @Override
    public TextRange getDescriptionTextRange() {
        return getComponentTextRange(Component.DESCRIPTION);
    }

    @NotNull
    @Override
    public List<String> getLicences() {
        return getTextComponents(Component.LICENCE);
    }

    @NotNull
    public List<String> getURLs() {
        return getTextComponents(Component.URL);
    }

    private void addRootTag() {
        file.add(XmlElementFactory.getInstance(file.getProject()).createTagFromText("<package>\r\n</package>"));
    }

    @Nullable
    private String getTextComponent(Component component) {
        if (file.getRootTag() == null) {
            return null;
        }
        return file.getRootTag().getSubTagText(component.get());
    }

    @NotNull
    private List<String> getTextComponents(Component component) {
        if (file.getRootTag() == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(file.getRootTag().findSubTags(component.get()))
                .map(tag -> tag.getValue().getText())
                .collect(Collectors.toList());
    }

    private void setComponent(Component component, String newContent) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        XmlTag[] nameTags = file.getRootTag().findSubTags(component.get());
        if (nameTags.length == 0) {
            file.getRootTag().addSubTag(file.getRootTag()
                    .createChildTag(component.get(), null, newContent, false), true);
        } else if (nameTags.length > 1) {
            nameTags[0].getValue().setText(newContent);
            for (int i = 1; i < nameTags.length; i++) {
                nameTags[i].delete();
            }
        } else {
            nameTags[0].getValue().setText(newContent);
        }
    }

    private TextRange getComponentTextRange(Component component) {
        if (file.getRootTag() == null) {
            return file.getTextRange();
        }
        XmlTag tag = file.getRootTag().findFirstSubTag(component.get());
        if (tag == null) {
            return getRootTextRange();
        }
        return tag.getValue().getTextRange();
    }


    @NotNull
    private List<TextRange> getComponentTextRanges(Component component) {
        if (file.getRootTag() == null) {
            return Collections.singletonList(file.getTextRange());
        }
        List<TextRange> ret = Arrays.stream(file.getRootTag().findSubTags(component.get()))
                .map(tag -> tag.getValue().getTextRange())
                .collect(Collectors.toList());
        return ret.isEmpty() ? Collections.singletonList(getRootTextRange()) : ret;
    }

    @NotNull
    @Override
    public List<TextRange> getLicenceTextRanges() {
        return getComponentTextRanges(Component.LICENCE);
    }

    @NotNull
    @Override
    public List<TextRange> getURLTextRanges() {
        return getComponentTextRanges(Component.URL);
    }

    @Override
    public void addLicence(String newLicence) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        file.getRootTag().addSubTag(file.getRootTag()
                .createChildTag(Component.LICENCE.get(),
                        null, newLicence, false), true);
    }

    @NotNull
    @Override
    public List<Contributor> getMaintainers() {
        return getContributorComponents(Component.MAINTAINER);
    }

    @NotNull
    @Override
    public List<Contributor> getAuthors() {
        return getContributorComponents(Component.AUTHOR);
    }

    @NotNull
    private List<Contributor> getContributorComponents(Component component) {
        if (file.getRootTag() == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(file.getRootTag().findSubTags(component.get()))
                .map(tag -> new Contributor(tag.getValue().getText(),
                        Optional.ofNullable(tag.getAttributeValue("email")).orElse("")))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public List<TextRange> getMaintainerTextRanges() {
        return getComponentTextRanges(Component.MAINTAINER);
    }

    @NotNull
    @Override
    public List<TextRange> getAuthorTextRanges() {
        return getComponentTextRanges(Component.AUTHOR);
    }

    @Override
    public void addMaintainer(String name, String email) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        XmlTag newTag = file.getRootTag()
                .createChildTag(Component.MAINTAINER.get(),
                        null, name, false);
        newTag.setAttribute("email", email);
        file.getRootTag().addSubTag(newTag, true);
    }

    private boolean setContributor(int id, @NotNull Contributor contributor, @NotNull Component component) {
        XmlTag contribTag = Objects.requireNonNull(file.getRootTag()).findSubTags(component.get())[id];
        boolean ret = !contributor.getName().equals(contribTag.getValue().getText());
        XmlTag newTag = file.getRootTag()
                .createChildTag(component.get(),
                        null, contributor.getName(), false);
        newTag.setAttribute("email", contributor.getEmail());
        contribTag.replace(newTag);
        return ret;
    }

    @Override
    public boolean setAuthor(int id, @NotNull Contributor contributor) {
        return setContributor(id, contributor, Component.AUTHOR);
    }

    @Override
    public boolean setMaintainer(int id, @NotNull Contributor contributor) {
        return setContributor(id, contributor, Component.MAINTAINER);
    }

    private void removeComponent(int id, @NotNull Component component) {
        Objects.requireNonNull(file.getRootTag()).findSubTags(component.get())[id].delete();
    }

    @Override
    public void removeAuthor(int id) {
        removeComponent(id, Component.AUTHOR);
    }

    @Override
    public void removeMaintainer(int id) {
        removeComponent(id, Component.MAINTAINER);
    }

    @Override
    public void removeURL(int id) {
        removeComponent(id, Component.URL);
    }

    @Override
    public XmlTag[] findSubTags(String qName) {
        if (file.getRootTag() == null) {
            return new XmlTag[0];
        }
        return file.getRootTag().findSubTags(qName);
    }
}
