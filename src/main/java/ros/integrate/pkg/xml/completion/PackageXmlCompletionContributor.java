package ros.integrate.pkg.xml.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.EmptyConsumer;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.*;
import ros.integrate.pkg.xml.condition.psi.ROSConditionTypes;
import ros.integrate.settings.ROSSettings;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageXmlCompletionContributor extends CompletionContributor {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.pkg.xml.completion.PackageXmlCompletionContributor");
    private static final String[] BUILD_TYPES = loadBuildTypes();
    private static final List<String> COND_TAGS = Arrays.asList("build_type", "group_depend", "member_of_group");

    @NotNull
    private static String[] loadBuildTypes() {
        Properties ret = new Properties();
        try {
            ret.load(ROSSettings.class.getClassLoader().getResourceAsStream("defaults.properties"));
            return ret.getProperty("buildTypes").split(":");
        } catch (IOException e) {
            LOG.warning("could not load configuration file, default values will not be loaded. error: " +
                    e.getMessage());
        }
        return new String[0];
    }

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
                            addCompletionsForAttrValue(tag, resultSet, PackageXmlUtil.getAttributeName(element), parameters);
                        } else if (type.equals(XmlElementType.XML_NAME) && element.getParent() instanceof XmlAttribute) {
                            setCompletionsForAttrName(tag, xmlFile, resultSet, parameters);
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
        if (tag.getName().equals("license")) { // WebReferences do not get completion
            List<String> xmlLicenses = xmlFile.getLicences().stream()
                    .map(ROSPackageXml.License::getValue).collect(Collectors.toList());
            ROSLicenses.AVAILABLE_LICENSES.keySet().stream().filter(license -> !xmlLicenses.contains(license))
                    .map(LookupElementBuilder::create).forEach(resultSet::addElement);
        } else if (PackageXmlUtil.isDependencyTag(tag)) {
            int format = xmlFile.getFormat();
            Collection<ROSPackage> packages = new HashSet<>();
            packages.addAll(tag.getProject().getService(ROSPackageManager.class).getAllPackages());
            packages.addAll(tag.getProject().getService(ROSDepKeyCache.class).getAllKeys());
            Arrays.stream(PackageXmlUtil.getDependencyType(tag).getCoveredDependencies())
                    .map(xmlFile::getDependencies).map(deps -> deps.stream()
                    .filter(dep -> !PackageXmlUtil.conditionEvaluatesToFalse(dep, format))
                    .map(ROSPackageXml.Dependency::getPackage)
                    .collect(Collectors.toList()))
                    .forEach(packages::removeAll);
            packages.remove(xmlFile.getPackage());
            packages.stream().map(pkg -> LookupElementBuilder.create(pkg).withIcon(pkg.getIcon(0)))
                    .forEach(resultSet::addElement);
        } else if (tag.getName().equals("build_type")) {
            Arrays.stream(BUILD_TYPES).map(LookupElementBuilder::create).map(LookupElementBuilder::bold)
                    .forEach(resultSet::addElement);
        } else if (tag.getName().equals("member_of_group") || tag.getName().equals("group_depend") &&
                xmlFile.getFormat() >= 3) {
            Set<String> result = new HashSet<>();
            Set<String> badGroups =
                    Stream.concat(xmlFile.getGroups().stream(), xmlFile.getGroupDepends().stream())
                    .filter(group -> !PackageXmlUtil.conditionEvaluatesToFalse(group, xmlFile.getFormat()))
                    .map(ROSPackageXml.GroupLink::getGroup).collect(Collectors.toSet());
            tag.getProject().getService(ROSPackageManager.class).getAllPackages().stream()
                    .map(ROSPackage::getPackageXml).filter(Objects::nonNull)
                    .peek(pkgXml -> result.addAll(pkgXml.getGroups().stream()
                            .map(ROSPackageXml.GroupLink::getGroup).collect(Collectors.toSet())))
                    .forEach(pkgXml -> result.addAll(pkgXml.getGroupDepends().stream()
                            .map(ROSPackageXml.GroupLink::getGroup).collect(Collectors.toSet())));
            result.stream().filter(str -> !str.isEmpty() && !badGroups.contains(str))
                    .map(LookupElementBuilder::create)
                    .map(lookupElement -> lookupElement.withIcon(ROSIcons.GROUP))
                    .forEach(resultSet::addElement);
        }
    }

    private void setCompletionsForTagName(int level, @Nullable XmlTag parentTag, @NotNull ROSPackageXml xmlFile,
                                          @NotNull CompletionResultSet resultSet, CompletionParameters parameters) {
        resultSet.runRemainingContributors(parameters, EmptyConsumer.getInstance()); // removes all other entries. Dangerous stuff.
        int format = xmlFile.getFormat();
        InsertHandler<LookupElement> attrHandler = new AttributeNameHandler(null),
                dataHandler = new TagDataHandler("", false),
                multilineHandler = new TagDataHandler("", false, "", true, false),
                dataWithCompletionHandler = new TagDataHandler("", false, "", false, true);
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
                    resultSet.addElement(LookupElementBuilder.create("version")
                            .withInsertHandler(format >= 3 ? attrHandler : dataHandler));
                }
                if (xmlFile.getDescription() == null) {
                    resultSet.addElement(LookupElementBuilder.create("description").withInsertHandler(multilineHandler));
                }
                resultSet.addElement(LookupElementBuilder.create("url").withInsertHandler(attrHandler));
                resultSet.addElement(LookupElementBuilder.create("author").withInsertHandler(attrHandler));
                resultSet.addElement(LookupElementBuilder.create("maintainer")
                        .withInsertHandler(new AttributeNameHandler("email")));
                resultSet.addElement(LookupElementBuilder.create("license")
                        .withInsertHandler(format >= 3 ? attrHandler : dataWithCompletionHandler));
                PackageXmlUtil.getDependNames(format).stream().map(LookupElementBuilder::create)
                        .map(builder -> builder.withInsertHandler(attrHandler))
                        .forEach(resultSet::addElement);
                if (xmlFile.getExport() == null) {
                    resultSet.addElement(LookupElementBuilder.create("export").withInsertHandler(multilineHandler));
                }
                resultSet.addElement(LookupElementBuilder.create("group_depend").withInsertHandler(attrHandler));
                resultSet.addElement(LookupElementBuilder.create("member_of_group").withInsertHandler(attrHandler));
                return;
            }
            case 2: {
                ExportTag export = xmlFile.getExport();
                if (parentTag != null && parentTag.getName().equals("export") && export != null) {
                    if (export.getMessageGenerator() == null) {
                        resultSet.addElement(LookupElementBuilder.create("message_generator")
                                .withInsertHandler(dataHandler));
                    }
                    if (!export.markedArchitectureIndependent()) {
                        resultSet.addElement(LookupElementBuilder.create("architecture_independent")
                                .withInsertHandler(new EmptyTagHandler()));
                    }
                    if (export.deprecatedMessage() == null) {
                        resultSet.addElement(LookupElementBuilder.create("deprecated")
                                .withInsertHandler(multilineHandler));
                    }
                    if (!export.isMetapackage()) {
                        resultSet.addElement(LookupElementBuilder.create("metapackage")
                                .withInsertHandler(new EmptyTagHandler()));
                    }
                    if (export.getBuildTypes().stream().allMatch(buildType ->
                            PackageXmlUtil.conditionEvaluatesToFalse(buildType.getCondition(), format))) {
                        resultSet.addElement(LookupElementBuilder.create("build_type")
                                .withInsertHandler(format >= 3 ? attrHandler : dataWithCompletionHandler));
                    }
                }
                return;
            }
            default:
        }
    }

    private void setCompletionsForAttrName(@NotNull XmlTag tag, @NotNull ROSPackageXml xmlFile,
                                           @NotNull CompletionResultSet resultSet, CompletionParameters parameters) {
        int format = xmlFile.getFormat();
        InsertHandler<LookupElement> anyValueHandler = new AttributeValueHandler(false),
                completeValueHandler = new AttributeValueHandler(true);
        resultSet.runRemainingContributors(parameters, EmptyConsumer.getInstance()); // removes all other entries. Dangerous stuff.
        if (tag.getName().equals("version") && tag.getAttribute("compatibility") == null && format >= 3) {
            resultSet.addElement(LookupElementBuilder.create("compatibility").withInsertHandler(completeValueHandler));
            resultSet.addElement(LookupElementBuilder.create("").withTailText("default", true)
                    .withInsertHandler(new SkipAttributeHandler(false)));
        }
        if (tag.getName().equals("license") && tag.getAttribute("file") == null && format >= 3) {
            resultSet.addElement(LookupElementBuilder.create("file").withInsertHandler(completeValueHandler));
            resultSet.addElement(LookupElementBuilder.create("").withTailText("no file", true)
                        .withInsertHandler(new SkipAttributeHandler(true)));
        }
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
            if (tag.getAttribute("condition") == null && format >= 3) {
                resultSet.addElement(LookupElementBuilder.create("condition").withInsertHandler(anyValueHandler));
            }
            resultSet.addElement(LookupElementBuilder.create("").withTailText("move to name", true)
                    .withInsertHandler(new SkipAttributeHandler(true)));
        }
        if (COND_TAGS.contains(tag.getName())) {
            if (tag.getAttribute("condition") == null && format >= 3) {
                resultSet.addElement(LookupElementBuilder.create("condition").withInsertHandler(anyValueHandler));
            }
            resultSet.addElement(LookupElementBuilder.create("").withTailText("unconditional", true)
                    .withInsertHandler(new SkipAttributeHandler(true)));
        }
    }

    private void addCompletionsForAttrValue(@NotNull XmlTag tag, @NotNull CompletionResultSet resultSet,
                                            @Nullable String attributeName, CompletionParameters parameters) {
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
                    .map(ROSPackageXml::getVersion).map(ROSPackageXml.Version::getValue).orElse("");
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
        if (attributeName != null && attributeName.equals("condition")) {
            InjectedLanguageManager manager = InjectedLanguageManager.getInstance(tag.getProject());
            Optional.ofNullable(parameters.getOriginalPosition())
                    .map(PsiElement::getParent).map(manager::getInjectedPsiFiles)
                    .filter(list -> !list.isEmpty()).map(list -> list.get(0))
                    .map(pair -> pair.first).ifPresent(injected -> {
                int offset = parameters.getEditor().getCaretModel().getOffset() - injected.getTextOffset() - 1;
                PsiElement injectedPos = injected.findElementAt(offset);
                if (!(injectedPos instanceof ASTNode)) {
                    return;
                }
                CompletionResultSet injectedResult = resultSet.withPrefixMatcher(injectedPos.getText());
                if (((ASTNode) injectedPos).getElementType().equals(ROSConditionTypes.VARIABLE)) {
                    for (String env : System.getenv().keySet()) {
                        injectedResult.addElement(LookupElementBuilder.create("$" + env).withPresentableText(env));
                    }
                }
                if (((ASTNode) injectedPos).getElementType().equals(ROSConditionTypes.LITERAL)) {
                    injectedResult.addElement(LookupElementBuilder.create("or").bold());
                    injectedResult.addElement(LookupElementBuilder.create("and").bold());
                }
            });
        }
    }
}
