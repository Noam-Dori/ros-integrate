package ros.integrate.pkg.xml.impl;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ROSPackageXmlImpl implements ROSPackageXml {
    private static final String EXPORT = "export", FORMAT = "format", EMAIL = "email";

    private enum Component {
        NAME,
        VERSION,
        DESCRIPTION,
        MAINTAINER,
        LICENSE,
        AUTHOR,
        URL;
        
        @NotNull
        String get() {
            return name().toLowerCase();
        }
    }

    private static final List<String> TAG_NAMES = findTagNames();

    private static List<String> findTagNames() {
        List<DependencyType> dependencyTypes = Arrays.asList(DependencyType.values());
        dependencyTypes.sort(DependencyType::compare);
        return Stream.concat(Stream.concat(Stream.of(Component.values()).map(Component::get),
                dependencyTypes.stream().map(DependencyType::getTagName)), Stream.of(EXPORT))
                .collect(Collectors.toList());
    }

    private XmlFile file;
    private final ROSPackage pkg;
    private ROSPackageManager pkgManager;
    private final ROSDepKeyCache keyCache;

    @Contract(pure = true)
    public ROSPackageXmlImpl(@NotNull XmlFile xmlToWrap, @NotNull ROSPackage pkg) {
        file = xmlToWrap;
        pkgManager = file.getProject().getService(ROSPackageManager.class);
        keyCache = file.getProject().getService(ROSDepKeyCache.class);
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
        pkgManager = file.getProject().getService(ROSPackageManager.class);
    }

    @Override
    public int getFormat() {
        if (file.getRootTag() == null) {
            return 0;
        }
        XmlAttribute format = file.getRootTag().getAttribute(FORMAT);
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
        XmlAttribute format = file.getRootTag().getAttribute(FORMAT);
        if (format == null || format.getValueElement() == null) {
            return getRootTextRange();
        }
        return format.getValueElement().getValueTextRange();
    }

    @NotNull
    @Override
    public ROSPackage getPackage() {
        return pkg;
    }

    @Override
    public String getPkgName() {
        return getTextComponent(Component.NAME);
    }

    @NotNull
    private TextRange getRootTextRange() {
        if (file.getRootTag() == null) {
            return file.getTextRange();
        }
        return new TextRange(file.getRootTag().getTextOffset() + 1, file.getRootTag().getTextOffset() + 1 +
                file.getRootTag().getName().length());
    }

    @NotNull
    @Override
    public TextRange getNameTextRange() {
        return getComponentTextRange(Component.NAME);
    }

    @Override
    public void setFormat(int format) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        file.getRootTag().setAttribute(FORMAT, String.valueOf(format));
    }

    @Override
    public void setPkgName(@NotNull String pkgName) {
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
    public void setVersion(@NotNull String newVersion) {
        setComponent(Component.VERSION, newVersion);
    }

    @Override
    public String getDescription() {
        return getTextComponent(Component.DESCRIPTION);
    }

    @Override
    public void setDescription(@NotNull String newDescription) {
        setComponent(Component.DESCRIPTION, newDescription);
    }

    @NotNull
    @Override
    public TextRange getDescriptionTextRange() {
        return getComponentTextRange(Component.DESCRIPTION);
    }

    @NotNull
    @Override
    public List<String> getLicences() {
        if (file.getRootTag() == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(file.getRootTag().findSubTags(Component.LICENSE.get()))
                .map(tag -> tag.getValue().getText())
                .collect(Collectors.toList());
    }

    @NotNull
    public List<Pair<String, URLType>> getURLs() {
        if (file.getRootTag() == null) {
            return new ArrayList<>(0);
        }
        XmlTag[] tags = file.getRootTag().findSubTags(Component.URL.get());
        List<Pair<String, URLType>> ret = new ArrayList<>(tags.length);
        Arrays.stream(tags).forEach(tag -> ret.add(findURLType(tag)));
        return ret;
    }

    @NotNull
    @Contract("_ -> new")
    private Pair<String, URLType> findURLType(@NotNull XmlTag tag) {
        String key = tag.getAttributeValue("type");
        if (key == null) {
            key = URLType.WEBSITE.name().toLowerCase();
        }
        for (URLType type : URLType.values()) {
            if (type.name().toLowerCase().equals(key)) {
                return new Pair<>(tag.getValue().getText(), type);
            }
        }
        return new Pair<>(tag.getValue().getText(), null);
    }

    private void addRootTag() {
        file.getFirstChild().replace(PsiFileFactory.getInstance(file.getProject())
                .createFileFromText("dummy.xml", XmlFileType.INSTANCE, "<package>\n</package>")
                .getFirstChild());
    }

    @Nullable
    private String getTextComponent(@NotNull Component component) {
        if (file.getRootTag() == null) {
            return null;
        }
        return file.getRootTag().getSubTagText(component.get());
    }

    private void setComponent(@NotNull Component component,@NotNull String newContent) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        XmlTag[] nameTags = file.getRootTag().findSubTags(component.get());
        if (nameTags.length == 0) {
            addLevel2Tag(file.getRootTag().createChildTag(component.get(), null, newContent,
                    false));
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
        return getComponentTextRanges(Component.LICENSE);
    }

    @NotNull
    @Override
    public List<TextRange> getURLTextRanges() {
        return getComponentTextRanges(Component.URL);
    }

    @Override
    public void addLicence(@NotNull String licenseName) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        addLevel2Tag(file.getRootTag().createChildTag(Component.LICENSE.get(), null, licenseName,
                false));
    }

    @Override
    public void addURL(@NotNull String url, @NotNull URLType type) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        XmlTag newTag = file.getRootTag()
                .createChildTag(Component.URL.get(), null, url, false);
        newTag.setAttribute(EMAIL, type.name().toLowerCase());
        addLevel2Tag(newTag);
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
                        Optional.ofNullable(tag.getAttributeValue(EMAIL)).orElse("")))
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

    @NotNull
    @Override
    public List<Pair<TextRange, TextRange>> getDependencyTextRanges() {
        if (file.getRootTag() == null) {
            return Collections.singletonList(new Pair<>(null, file.getTextRange()));
        }
        Stream<XmlTag> result = Stream.empty();
        for (DependencyType dep : DependencyType.values()) {
            result = Stream.concat(result, Stream.of(file.getRootTag().findSubTags(dep.getTagName())));
        }
        List<Pair<TextRange, TextRange>> ret = result.map(tag ->
                new Pair<>(new TextRange(tag.getTextOffset() + 1, tag.getTextOffset() + tag.getName().length() + 1),
                        tag.getValue().getTextRange()))
                .collect(Collectors.toList());
        return ret.isEmpty() ? Collections.singletonList(new Pair<>(null, getRootTextRange())) : ret;
    }

    @Override
    public void addMaintainer(@NotNull String name, @NotNull String email) {
        addContributor(name, email, Component.MAINTAINER);
    }

    @Override
    public void addAuthor(@NotNull String name, @Nullable String email) {
        addContributor(name, email, Component.AUTHOR);
    }

    @Override
    public void addDependency(@NotNull DependencyType type, @NotNull ROSPackage pkg,
                              @NotNull VersionRange versionRange,
                              boolean checkRepeating) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        if (checkRepeating) {
            Set<DependencyType> types = new HashSet<>();
            getDependencies(null).stream().filter(dep -> dep.getPackage() == pkg)
                    .map(Dependency::getType).map(DependencyType::getCoveredDependencies)
                    .map(Arrays::asList).forEach(types::addAll);
            types.retainAll(Arrays.asList(type.getCoveredDependencies()));
            if (!types.isEmpty()) {
                return;
            }
        }
        XmlTag newTag = file.getRootTag()
                .createChildTag(type.getTagName(), null, pkg.getName(), false);
        addVersionRangeToDep(newTag, versionRange);
        addLevel2Tag(newTag);
    }

    void addVersionRangeToDep(XmlTag depTag, @NotNull VersionRange range) {
        if (!range.isNotValid()) {
            if (range.getMax() != null) {
                if (range.getMax().equals(range.getMin())) {
                    depTag.setAttribute("version_eq", range.getMax());
                } else {
                    depTag.setAttribute("version_lt" + (range.isStrictMax() ? "" : "e"), range.getMax());
                }
            }
            if (range.getMin() != null && !range.getMin().equals(range.getMax())) {
                depTag.setAttribute("version_gt" + (range.isStrictMin() ? "" : "e"), range.getMin());
            }
        }
    }

    private void addContributor(@NotNull String name, @Nullable String email, Component component) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        XmlTag newTag = file.getRootTag()
                .createChildTag(component.get(), null, name, false);
        if (email != null) {
            newTag.setAttribute(EMAIL, email);
        }
        addLevel2Tag(newTag);
    }

    private void addLevel2Tag(XmlTag newTag) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        // find tag to add thing after
        int ordinal = TAG_NAMES.indexOf(newTag.getName());
        XmlTag anchor;
        do {
            XmlTag[] found = file.getRootTag().findSubTags(TAG_NAMES.get(ordinal));
            anchor = found.length == 0 ? null : found[found.length - 1];
            ordinal--;
        } while (ordinal >= 0 && anchor == null);
        if (anchor == null) {
            file.getRootTag().addSubTag(newTag, true);
        } else {
            file.getRootTag().addAfter(newTag, anchor);
        }
    }

    private boolean setContributor(int id, @NotNull Contributor contributor, @NotNull Component component) {
        XmlTag contribTag = Objects.requireNonNull(file.getRootTag()).findSubTags(component.get())[id];
        boolean ret = !contributor.getName().equals(contribTag.getValue().getText());
        XmlTag newTag = file.getRootTag()
                .createChildTag(component.get(),
                        null, contributor.getName(), false);
        if (!contributor.getEmail().isEmpty()) {
            newTag.setAttribute(EMAIL, contributor.getEmail());
        }
        contribTag.replace(newTag);
        return ret;
    }

    @Override
    public boolean setAuthor(int id, @NotNull Contributor contributor) {
        return setContributor(id, contributor, Component.AUTHOR);
    }

    @Override
    public void setDependency(int id, @NotNull Dependency dependency) {
        XmlTag root = Objects.requireNonNull(file.getRootTag());
        Stream<XmlTag> result = Stream.empty();
        for (DependencyType depType : DependencyType.values()) {
            result = Stream.concat(result, Stream.of(root.findSubTags(depType.getTagName())));
        }
        XmlTag oldTag = result.collect(Collectors.toList()).get(id);
        XmlTag newTag = root.createChildTag(dependency.getType().getTagName(), null,
                dependency.getPackage().getName(), false);
        addVersionRangeToDep(newTag, dependency.getVersionRange());
        oldTag.replace(newTag);
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
    public void setURL(int id, @NotNull String url, @NotNull URLType type) {
        XmlTag urlTag = Objects.requireNonNull(file.getRootTag()).findSubTags(Component.URL.get())[id];
        XmlTag newTag = file.getRootTag().createChildTag(Component.URL.get(),
                null, url, false);
        if (type != URLType.WEBSITE) {
            newTag.setAttribute("type", type.name().toLowerCase());
        }
        urlTag.replace(newTag);
    }

    @Override
    public void removeLicense(int id) {
        removeComponent(id, Component.LICENSE);
    }

    @Override
    public void removeDependency(int id) {
        if (file.getRootTag() == null) {
            return;
        }
        Stream<XmlTag> result = Stream.empty();
        for (DependencyType dep : DependencyType.values()) {
            result = Stream.concat(result, Stream.of(file.getRootTag().findSubTags(dep.getTagName())));
        }
        result.collect(Collectors.toList()).get(id).delete();
    }

    @Override
    public void setLicense(int id, @NotNull String licenseName) {
        Objects.requireNonNull(file.getRootTag()).findSubTags(Component.LICENSE.get())[id]
                .replace(file.getRootTag().createChildTag(Component.LICENSE.get(),
                        null, licenseName, false));
    }

    @NotNull
    @Override
    public XmlTag[] findSubTags(@NotNull String qName) {
        if (file.getRootTag() == null) {
            return new XmlTag[0];
        }
        return file.getRootTag().findSubTags(qName);
    }

    @NotNull
    @Override
    public List<Dependency> getDependencies(@Nullable DependencyType dependencyType) {
        if (file.getRootTag() == null) {
            return Collections.emptyList();
        }
        Stream<Pair<DependencyType, XmlTag>> result = Stream.empty();
        for (DependencyType dep : dependencyType == null ?
                DependencyType.values() : dependencyType.getCoveringTags(getFormat())) {
            result = Stream.concat(result, Stream.of(file.getRootTag().findSubTags(dep.getTagName()))
                    .map(tag -> new Pair<>(dep, tag)));
        }
        return result.map(pair -> new Dependency(pair.first, findPackage(pair.second.getValue().getText()),
                PackageXmlUtil.getVersionRange(pair.second))).collect(Collectors.toList());
    }

    @NotNull
    private ROSPackage findPackage(String name) {
        return Optional.ofNullable(pkgManager.findPackage(name)).map(Optional::of)
                .orElseGet(() -> Optional.ofNullable(keyCache.findKey(name))).orElse(ROSPackage.ORPHAN);
    }

    @Nullable
    @Override
    public ExportTag getExport() {
        if (file.getRootTag() == null) {
            return null;
        }
        return Optional.ofNullable(file.getRootTag().findFirstSubTag(EXPORT))
                .map(tag -> new ExportTagImpl(tag, this)).orElse(null);
    }

    @Override
    public void setExport(@NotNull XmlTag exportToRead) {
        if (file.getRootTag() == null) {
            addRootTag();
        }
        XmlTag oldExport = Optional.ofNullable(getExport()).map(ExportTag::getRawTag).orElse(null),
                newExport = file.getRootTag().createChildTag(EXPORT, null, exportToRead.getValue()
                        .getText(), false);
        if (oldExport == null) {
            addLevel2Tag(newExport);
        } else {
            oldExport.replace(newExport);
        }
    }
}
