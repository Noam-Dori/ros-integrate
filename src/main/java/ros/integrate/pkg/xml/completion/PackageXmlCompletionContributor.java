package ros.integrate.pkg.xml.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class PackageXmlCompletionContributor extends CompletionContributor {
    public PackageXmlCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(XmlToken.class).withLanguage(XMLLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    /**
                     * special completion for values within tags.
                     */
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        ROSPackageXml xmlFile = PackageXmlUtil.getWrapper(parameters.getOriginalFile());
                        if (xmlFile == null) {
                            return;
                        }
                        XmlToken element = (XmlToken) parameters.getPosition();
                        IElementType type = element.getTokenType();
                        XmlTag tag = PackageXmlUtil.getParentTag(element);
                        if (tag == null) {
                            return;
                        }
                        if (type.equals(XmlElementType.XML_ATTRIBUTE_VALUE_TOKEN)) {
                            addCompletionsForAttrValue(tag, resultSet, PackageXmlUtil.getAttributeName(element));
                        } else if (type.equals(XmlElementType.XML_NAME) && element.getParent() instanceof XmlAttribute) {
                            setCompletionsForAttrName(tag, resultSet, parameters);
                        } else if (type.equals(XmlElementType.XML_NAME)) {
                            setCompletionsForTagName(getLevel(tag), tag.getParentTag(), xmlFile, resultSet, parameters);
                        } else if (type.equals(XmlElementType.XML_DATA_CHARACTERS)) {
                            addCompletionsForTagValue(tag, xmlFile, resultSet, element);
                        }
                    }
                });
    }

    private int getLevel(@NotNull XmlTag tag) {
        int i = -1;
        while (tag != null) {
            tag = tag.getParentTag();
            i++;
        }
        return i;
    }

    private void addCompletionsForTagValue(@NotNull XmlTag tag, ROSPackageXml xmlFile, CompletionResultSet resultSet,
                                           @NotNull XmlToken element) {
        if (!element.getParent().getFirstChild().equals(element)) {
            return; // there is a bug where we cannot process XmlText so we only autocomplete on first non-space item
        }
        if (tag.getName().equals("license")) { // WebReferences do not get autocompletion
            ROSLicenses.AVAILABLE_LICENSES.keySet().stream().filter(license -> !xmlFile.getLicences().contains(license))
                    .map(LookupElementBuilder::create).forEach(resultSet::addElement);
        } else if (PackageXmlUtil.isDependencyTag(tag)) {
            Collection<ROSPackage> packages = new HashSet<>();
            packages.addAll(tag.getProject().getService(ROSPackageManager.class).getAllPackages());
            packages.addAll(tag.getProject().getService(ROSDepKeyCache.class).getAllKeys());
            Arrays.stream(PackageXmlUtil.getDependencyType(tag).getCoveredDependencies())
                    .map(xmlFile::getDependencies).map(dep -> dep.stream().map(ROSPackageXml.Dependency::getPackage)
                    .collect(Collectors.toList()))
                    .forEach(packages::removeAll);
            packages.remove(xmlFile.getPackage());
            packages.stream().map(pkg -> LookupElementBuilder.create(pkg).withIcon(pkg.getIcon(0)))
                    .forEach(resultSet::addElement);
        }
    }

    private void setCompletionsForTagName(int level, @Nullable XmlTag parentTag, @NotNull ROSPackageXml xmlFile,
                                          @NotNull CompletionResultSet resultSet, CompletionParameters parameters) {
        resultSet.runRemainingContributors(parameters, result -> {
        }); // removes all other entries. Dangerous stuff.
        InsertHandler<LookupElement> attrHandler =
                new AttributeNameHandler(null),
                dataHandler = new TagDataHandler("", false),
                multilineHandler = new TagDataHandler("", false, "", true, false);
        switch (level) {
            case 0: {
                if (xmlFile.getRawXml().getRootTag() == null || xmlFile.getRawXml().getRootTag().getName().isEmpty()) {
                    resultSet.addElement(LookupElementBuilder.create("package").withInsertHandler(multilineHandler));
                }
                return;
            }
            case 1: {
                if (xmlFile.getPkgName() == null) {
                    resultSet.addElement(LookupElementBuilder.create("name")
                            .withInsertHandler(new TagDataHandler("", false, xmlFile.getPackage().getName(), false, false)));
                }
                if (xmlFile.getVersion() == null) {
                    resultSet.addElement(LookupElementBuilder.create("version").withInsertHandler(dataHandler));
                }
                if (xmlFile.getDescription() == null) {
                    resultSet.addElement(LookupElementBuilder.create("description").withInsertHandler(multilineHandler));
                }
                resultSet.addElement(LookupElementBuilder.create("url").withInsertHandler(attrHandler));
                resultSet.addElement(LookupElementBuilder.create("author").withInsertHandler(attrHandler));
                resultSet.addElement(LookupElementBuilder.create("maintainer")
                        .withInsertHandler(new AttributeNameHandler("email")));
                resultSet.addElement(LookupElementBuilder.create("license")
                        .withInsertHandler(new TagDataHandler("", false, "", false, true)));
                PackageXmlUtil.getDependNames(xmlFile.getFormat()).stream().map(LookupElementBuilder::create)
                        .map(builder -> builder.withInsertHandler(attrHandler))
                        .forEach(resultSet::addElement);
                if (xmlFile.getExport() == null) {
                    resultSet.addElement(LookupElementBuilder.create("export").withInsertHandler(multilineHandler));
                }
                return;
            }
            case 2: {
                ExportTag export = xmlFile.getExport();
                if (parentTag != null && parentTag.getName().equals("export") && export != null) {
                    if (export.getMessageGenerator() == null) {
                        resultSet.addElement(LookupElementBuilder.create("message_generator")
                                .withInsertHandler(dataHandler));
                    }
                    if (!export.isArchitectureIndependent()) {
                        resultSet.addElement(LookupElementBuilder.create("architecture_independent")
                                .withInsertHandler(new EmptyTagHandler()));
                    }
                }
                return;
            }
            default:
        }
    }

    private void setCompletionsForAttrName(@NotNull XmlTag tag, @NotNull CompletionResultSet resultSet,
                                           CompletionParameters parameters) {
        InsertHandler<LookupElement> anyValueHandler = new AttributeValueHandler(false),
                completeValueHandler = new AttributeValueHandler(true);
        resultSet.runRemainingContributors(parameters, result -> {}); // removes all other entries. Dangerous stuff.
        if (tag.getName().equals("url") && tag.getAttribute("type") == null) {
            resultSet.addElement(LookupElementBuilder.create("type").withInsertHandler(completeValueHandler));
            resultSet.addElement(LookupElementBuilder.create("").withTailText("default", true)
                    .withInsertHandler(new SkipAttributeHandler(false)));
        }
        if (tag.getName().matches("author|maintainer") && tag.getAttribute("email") == null) {
            resultSet.addElement(LookupElementBuilder.create("email").withInsertHandler(anyValueHandler));
            if (tag.getName().equals("author")) {
                resultSet.addElement(LookupElementBuilder.create("").withTailText("no email", true)
                        .withInsertHandler(new SkipAttributeHandler(false)));
            }
        }
        if (PackageXmlUtil.isDependencyTag(tag)) {
            VersionRange range = PackageXmlUtil.getVersionRange(tag);
            if (range.getMin() == null && range.getMax() == null) {
                resultSet.addElement(LookupElementBuilder.create("version_eq").withInsertHandler(completeValueHandler));
            }
            if (range.getMin() == null) {
                resultSet.addElement(LookupElementBuilder.create("version_gt").withInsertHandler(completeValueHandler));
                resultSet.addElement(LookupElementBuilder.create("version_gte").withInsertHandler(completeValueHandler));
            }
            if (range.getMax() == null) {
                resultSet.addElement(LookupElementBuilder.create("version_lt").withInsertHandler(completeValueHandler));
                resultSet.addElement(LookupElementBuilder.create("version_lte").withInsertHandler(completeValueHandler));
            }
            resultSet.addElement(LookupElementBuilder.create("").withTailText("move to name", true)
                    .withInsertHandler(new SkipAttributeHandler(true)));
        }
    }

    private void addCompletionsForAttrValue(@NotNull XmlTag tag, @NotNull CompletionResultSet resultSet,
                                            @Nullable String attributeName) {
        InsertHandler<LookupElement> tagDataHandler = new TagDataHandler(tag.getName(), true);
        if (tag.getName().equals("url")) {
            for (ROSPackageXml.URLType type : ROSPackageXml.URLType.values()) {
                resultSet.addElement(LookupElementBuilder.create(type.name().toLowerCase())
                        .withInsertHandler(tagDataHandler));
            }
        }
        if (PackageXmlUtil.isDependencyTag(tag)) {
            String pkgVersion = Optional.ofNullable(tag.getProject().getService(ROSPackageManager.class)
                    .findPackage(tag.getValue().getText())).map(ROSPackage::getPackageXml)
                    .map(ROSPackageXml::getVersion).orElse("");
            if ("version_eq".equals(attributeName)) {
                resultSet.addElement(LookupElementBuilder.create("1.0.0").withInsertHandler(tagDataHandler));
                if (!pkgVersion.isEmpty()) {
                    resultSet.addElement(LookupElementBuilder.create(pkgVersion).withInsertHandler(tagDataHandler));
                }
            }
            if (attributeName != null && attributeName.matches("version_[lg]te?")) {
                resultSet.addElement(LookupElementBuilder.create("1.0.0")
                        .withInsertHandler(new AttributeNameHandler()));
                if (!pkgVersion.isEmpty()) {
                    resultSet.addElement(LookupElementBuilder.create(pkgVersion)
                            .withInsertHandler(new AttributeNameHandler()));
                }
            }
        }
    }
}
