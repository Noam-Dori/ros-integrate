package ros.integrate.pkg.xml;

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
import org.jetbrains.annotations.Nullable;

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
        }
    }

    private void setCompletionsForTagName(@NotNull ROSPackageXml xmlFile, @NotNull CompletionResultSet resultSet,
                                          CompletionParameters parameters) {
        resultSet.runRemainingContributors(parameters, result -> {}); // removes all other entries. Dangerous stuff.
        InsertHandler<LookupElement> attrHandler =
                (context, item) -> handleCompleteAttr(context, item.getLookupString(), null),
                dataHandler = (context, item) -> handleMoveToData(context, item.getLookupString(), false);
        if (xmlFile.getPkgName() == null) {
            resultSet.addElement(LookupElementBuilder.create("name")
                    .withInsertHandler((context, item) ->
                            handleMoveToData(context, "name", false, xmlFile.getPackage().getName(), false)));
        }
        if (xmlFile.getVersion() == null) {
            resultSet.addElement(LookupElementBuilder.create("version").withInsertHandler((dataHandler)));
        }
        if (xmlFile.getDescription() == null) {
            resultSet.addElement(LookupElementBuilder.create("description")
                    .withInsertHandler((context, item) ->
                            handleMoveToData(context, "description", false, "", true)));
        }
        resultSet.addElement(LookupElementBuilder.create("url").withInsertHandler(attrHandler));
        resultSet.addElement(LookupElementBuilder.create("author").withInsertHandler(attrHandler));
        resultSet.addElement(LookupElementBuilder.create("maintainer")
                .withInsertHandler((context, item) -> handleCompleteAttr(context, "maintainer", "email")));
        resultSet.addElement(LookupElementBuilder.create("license").withInsertHandler(dataHandler));

    }

    private void setCompletionsForAttrName(@NotNull XmlTag tag, @NotNull CompletionResultSet resultSet,
                                           CompletionParameters parameters) {
        resultSet.runRemainingContributors(parameters, result -> {}); // removes all other entries. Dangerous stuff.
        if (tag.getName().equals("url") && tag.getAttribute("type") == null) {
            resultSet.addElement(LookupElementBuilder.create("type")
                    .withInsertHandler((context, item) -> handleAttr(context, true)));
            resultSet.addElement(LookupElementBuilder.create("").withTailText("default", true)
                    .withInsertHandler((context, item) -> handleNoAttr(context)));
        }
        if (tag.getName().matches("author|maintainer") && tag.getAttribute("email") == null) {
            resultSet.addElement(LookupElementBuilder.create("email")
                    .withInsertHandler((context, item) -> handleAttr(context, false)));
            if (tag.getName().equals("author")) {
                resultSet.addElement(LookupElementBuilder.create("").withTailText("no email", true)
                        .withInsertHandler((context, item) -> handleNoAttr(context)));
            }
        }
    }

    private void handleNoAttr(@NotNull InsertionContext insertionContext) {
        CaretModel model = insertionContext.getEditor().getCaretModel();
        int offset = model.getOffset();
        String text = insertionContext.getDocument().getText(new TextRange(offset, offset + 1));
        insertionContext.getDocument().deleteString(offset - 1, offset);
        if (text.equals(">")) {
            model.getCurrentCaret().moveCaretRelatively(1, 0, false, false);
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
            resultSet.addElement(LookupElementBuilder.create("website")
                    .withInsertHandler((context, item) -> handleMoveToData(context, tag.getName(), true)));
            resultSet.addElement(LookupElementBuilder.create("repository")
                    .withInsertHandler((context, item) -> handleMoveToData(context, tag.getName(), true)));
            resultSet.addElement(LookupElementBuilder.create("bugtracker")
                    .withInsertHandler((context, item) -> handleMoveToData(context, tag.getName(), true)));
        }
    }


    private static void handleMoveToData(@NotNull InsertionContext insertionContext, String tagName, boolean inAttr) {
        handleMoveToData(insertionContext, tagName, inAttr, "", false);
    }

    private static void handleMoveToData(@NotNull InsertionContext insertionContext, String tagName, boolean inAttr,
                                         @NotNull String data, boolean multiline) {
        CaretModel model = insertionContext.getEditor().getCaretModel();
        if (inAttr) {
            model.getCurrentCaret().moveCaretRelatively(1, 0, false, false);
        }
        int offset = model.getOffset();
        String text = insertionContext.getDocument().getText(new TextRange(offset, offset + 1)),
                insert = ">" + (multiline ? "\n        " : "") + data +
                        (multiline ? "\n    " : "") + "</" + tagName + ">";
        if ("\r\n".contains(text)) {
            insertionContext.getDocument().insertString(offset, insert);
        }
        model.getCurrentCaret().moveCaretRelatively(multiline ? 0 : data.isEmpty() ? 1 : insert.length(), 0,
                false, false);
        if (multiline) {
            model.getCurrentCaret().moveCaretRelatively(data.length() + 8, 1, false, false);
        }
        if (tagName.equals("license")) {
            newCompletion(insertionContext.getProject(), insertionContext.getEditor());
        }
    }

    private void handleCompleteAttr(@NotNull InsertionContext insertionContext, @NotNull String tagName, @Nullable String attrName) {
        CaretModel model = insertionContext.getEditor().getCaretModel();
        int offset = model.getOffset();
        String nextChar = insertionContext.getDocument().getText(new TextRange(offset, offset + 1)),
                attrInsert = attrName == null ? "" : attrName + "=\"\"";
        if ("\r\n".contains(nextChar)) {
            insertionContext.getDocument().insertString(offset, " " + attrInsert + "></" + tagName + ">");
        }
        model.getCurrentCaret().moveCaretRelatively(Math.max(attrInsert.length(), 1), 0, false, false);
        if (attrName == null) {
            newCompletion(insertionContext.getProject(), insertionContext.getEditor());
        }
    }

    private static void newCompletion(Project project, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> new CodeCompletionHandlerBase(CompletionType.BASIC, true, false, true)
                .invokeCompletion(project, editor));
    }
}
