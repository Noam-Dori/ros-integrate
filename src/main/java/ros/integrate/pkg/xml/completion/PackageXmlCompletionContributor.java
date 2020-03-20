package ros.integrate.pkg.xml.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ROSDepKeyCache;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSLicenses;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.VersionRange;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
                            addCompletionsForAttrValue(tag, resultSet);
                        } else if (type.equals(XmlElementType.XML_NAME) && element.getParent() instanceof XmlAttribute) {
                            setCompletionsForAttrName(tag, resultSet, parameters);
                        } else if (type.equals(XmlElementType.XML_NAME)) {
                            setCompletionsForTagName(xmlFile, resultSet, parameters);
                        } else if (type.equals(XmlElementType.XML_DATA_CHARACTERS)) {
                            addCompletionsForTagValue(tag, xmlFile, resultSet, element);
                        }
                    }
                });
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

    private void setCompletionsForTagName(@NotNull ROSPackageXml xmlFile, @NotNull CompletionResultSet resultSet,
                                          CompletionParameters parameters) {
        resultSet.runRemainingContributors(parameters, result -> {
        }); // removes all other entries. Dangerous stuff.
        InsertHandler<LookupElement> attrHandler =
                new AttributeNameHandler(null),
                dataHandler = new MoveToDataHandler("", false),
                multilineHandler = new MoveToDataHandler("", false, "", true, false);
        if (xmlFile.getRawXml().getRootTag() == null || xmlFile.getRawXml().getRootTag().getName().isEmpty()) {
            resultSet.addElement(LookupElementBuilder.create("package").withInsertHandler(multilineHandler));
            return;
        }
        if (xmlFile.getPkgName() == null) {
            resultSet.addElement(LookupElementBuilder.create("name")
                    .withInsertHandler(new MoveToDataHandler("", false, xmlFile.getPackage().getName(), false, false)));
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
                .withInsertHandler(new MoveToDataHandler("", false, "", false, true)));
        PackageXmlUtil.getDependNames(xmlFile.getFormat()).stream().map(LookupElementBuilder::create)
                .map(builder -> builder.withInsertHandler(attrHandler))
                .forEach(resultSet::addElement);
    }

    private void setCompletionsForAttrName(@NotNull XmlTag tag, @NotNull CompletionResultSet resultSet,
                                           CompletionParameters parameters) {
        InsertHandler<LookupElement> anyValueHandler = (context, item) -> handleAttr(context, false);
        resultSet.runRemainingContributors(parameters, result -> {}); // removes all other entries. Dangerous stuff.
        if (tag.getName().equals("url") && tag.getAttribute("type") == null) {
            resultSet.addElement(LookupElementBuilder.create("type")
                    .withInsertHandler((context, item) -> handleAttr(context, true)));
            resultSet.addElement(LookupElementBuilder.create("").withTailText("default", true)
                    .withInsertHandler((context, item) -> handleNoAttr(context, false)));
        }
        if (tag.getName().matches("author|maintainer") && tag.getAttribute("email") == null) {
            resultSet.addElement(LookupElementBuilder.create("email").withInsertHandler(anyValueHandler));
            if (tag.getName().equals("author")) {
                resultSet.addElement(LookupElementBuilder.create("").withTailText("no email", true)
                        .withInsertHandler((context, item) -> handleNoAttr(context, false)));
            }
        }
        if (PackageXmlUtil.isDependencyTag(tag)) {
            VersionRange range = PackageXmlUtil.getVersionRange(tag);
            if (range.getMin() == null && range.getMax() == null) {
                resultSet.addElement(LookupElementBuilder.create("version_eq").withInsertHandler(anyValueHandler));
            }
            if (range.getMin() == null) {
                resultSet.addElement(LookupElementBuilder.create("version_gt").withInsertHandler(anyValueHandler));
                resultSet.addElement(LookupElementBuilder.create("version_gte").withInsertHandler(anyValueHandler));
            }
            if (range.getMax() == null) {
                resultSet.addElement(LookupElementBuilder.create("version_lt").withInsertHandler(anyValueHandler));
                resultSet.addElement(LookupElementBuilder.create("version_lte").withInsertHandler(anyValueHandler));
            }
            resultSet.addElement(LookupElementBuilder.create("").withTailText("move to name", true)
                    .withInsertHandler((context, item) -> handleNoAttr(context, true)));
        }
    }

    private void handleNoAttr(@NotNull InsertionContext insertionContext, boolean newCompletion) {
        CaretModel model = insertionContext.getEditor().getCaretModel();
        int offset = model.getOffset();
        String text = insertionContext.getDocument().getText(new TextRange(offset, offset + 1));
        insertionContext.getDocument().deleteString(offset - 1, offset);
        if (text.equals(">")) {
            model.getCurrentCaret().moveCaretRelatively(1, 0, false, false);
        }
        if (newCompletion) {
            newCompletion(insertionContext.getProject(), insertionContext.getEditor());
        }
    }

    private void handleAttr(@NotNull InsertionContext insertionContext, boolean newCompletion) {
        CaretModel model = insertionContext.getEditor().getCaretModel();
        int offset = model.getOffset();
        insertionContext.getDocument().insertString(offset, "=\"\"");
        model.getCurrentCaret().moveCaretRelatively(2, 0, false, false);
        if (newCompletion) {
            newCompletion(insertionContext.getProject(), insertionContext.getEditor());
        }
    }

    private void addCompletionsForAttrValue(@NotNull XmlTag tag, CompletionResultSet resultSet) {
        if (tag.getName().equals("url")) {
            for (ROSPackageXml.URLType type : ROSPackageXml.URLType.values()) {
                resultSet.addElement(LookupElementBuilder.create(type.name().toLowerCase())
                        .withInsertHandler(new MoveToDataHandler(tag.getName(), true)));
            }
        }
    }

    private static void newCompletion(Project project, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> new CodeCompletionHandlerBase(CompletionType.BASIC, true, false, true)
                .invokeCompletion(project, editor));
    }
}
