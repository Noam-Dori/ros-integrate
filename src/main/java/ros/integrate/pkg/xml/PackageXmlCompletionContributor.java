package ros.integrate.pkg.xml;

import com.intellij.codeInsight.completion.*;
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
                        }
                        // XML_DATA_CHARACTERS: <url type="[website]">[http://example.com]</url>
                        // these are also split by space-bars. could be useful for licenses
                    }
                });
    }

    private void setCompletionsForTagName(@NotNull ROSPackageXml xmlFile, @NotNull CompletionResultSet resultSet,
                                          CompletionParameters parameters) {
        resultSet.runRemainingContributors(parameters, result -> {}); // removes all other entries. Dangerous stuff.
        if (xmlFile.getPkgName() == null) {
            resultSet.addElement(LookupElementBuilder.create("name")
                    .withInsertHandler((insertionContext, item) ->
                            handleMoveToData(insertionContext, "name", false, xmlFile.getPackage().getName(), false)));
        }
        if (xmlFile.getVersion() == null) {
            resultSet.addElement(LookupElementBuilder.create("version")
                    .withInsertHandler((insertionContext, item) -> handleMoveToData(insertionContext, "version", false)));
        }
        if (xmlFile.getDescription() == null) {
            resultSet.addElement(LookupElementBuilder.create("description")
                    .withInsertHandler((insertionContext, item) ->
                            handleMoveToData(insertionContext, "description", false, "", true)));
        }
        resultSet.addElement(LookupElementBuilder.create("url")
                .withInsertHandler((insertionContext, item) ->
                        handleCompleteAttr(insertionContext, "url", null)));
        resultSet.addElement(LookupElementBuilder.create("author")
                .withInsertHandler((insertionContext, item) ->
                        handleCompleteAttr(insertionContext, "author", null)));
        resultSet.addElement(LookupElementBuilder.create("maintainer")
                .withInsertHandler((insertionContext, item) ->
                        handleCompleteAttr(insertionContext, "maintainer", "email")));
        resultSet.addElement(LookupElementBuilder.create("license")
                .withInsertHandler((insertionContext, item) ->
                        handleMoveToData(insertionContext, "license", false)));

    }

    private void setCompletionsForAttrName(@NotNull XmlTag tag, CompletionResultSet resultSet,
                                           CompletionParameters parameters) {

    }

    private void addCompletionsForAttrValue(@NotNull XmlTag tag, CompletionResultSet resultSet) {
        if (tag.getName().equals("url")) {
            resultSet.addElement(LookupElementBuilder.create("website")
                    .withInsertHandler((insertionContext, item) -> handleMoveToData(insertionContext, tag.getName(), true)));
            resultSet.addElement(LookupElementBuilder.create("repository")
                    .withInsertHandler((insertionContext, item) -> handleMoveToData(insertionContext, tag.getName(), true)));
            resultSet.addElement(LookupElementBuilder.create("bugtracker")
                    .withInsertHandler((insertionContext, item) -> handleMoveToData(insertionContext, tag.getName(), true)));
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

    private void newCompletion(Project project, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> new CodeCompletionHandlerBase(CompletionType.BASIC, true, false, true)
                .invokeCompletion(project, editor));
    }
}
