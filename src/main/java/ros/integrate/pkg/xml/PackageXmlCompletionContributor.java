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
        resultSet.runRemainingContributors(parameters, result -> {}); // this removes all other entries. Dangerous stuff.
        resultSet.addElement(LookupElementBuilder.create("url")
                .withInsertHandler((insertionContext, item) -> handleUrl(insertionContext)));
    }

    private void setCompletionsForAttrName(@NotNull XmlTag tag, CompletionResultSet resultSet,
                                           CompletionParameters parameters) {

    }

    private void addCompletionsForAttrValue(@NotNull XmlTag tag, CompletionResultSet resultSet) {
        if (tag.getName().equals("url")) {
            resultSet.addElement(LookupElementBuilder.create("website")
                    .withInsertHandler((insertionContext, item) -> handleFinalAttribute(insertionContext, tag)));
            resultSet.addElement(LookupElementBuilder.create("repository")
                    .withInsertHandler((insertionContext, item) -> handleFinalAttribute(insertionContext, tag)));
            resultSet.addElement(LookupElementBuilder.create("bugtracker")
                    .withInsertHandler((insertionContext, item) -> handleFinalAttribute(insertionContext, tag)));
        }
    }

    private static void handleFinalAttribute(@NotNull InsertionContext insertionContext, XmlTag tag) {
        CaretModel model = insertionContext.getEditor().getCaretModel();
        model.getCurrentCaret().moveCaretRelatively(1,0,false, false);
        int offset = model.getOffset();
        String text = insertionContext.getDocument().getText(new TextRange(offset, offset + 1));
        if ("\r\n".contains(text)) {
            insertionContext.getDocument().insertString(offset,"></" + tag.getName() + ">");
        }
        model.getCurrentCaret().moveCaretRelatively(1,0,false, false);
    }

    private void handleUrl(@NotNull InsertionContext insertionContext) {
        CaretModel model = insertionContext.getEditor().getCaretModel();
        int offset = model.getOffset();
        String text = insertionContext.getDocument().getText(new TextRange(offset, offset + 1));
        if ("\r\n".contains(text)) {
            insertionContext.getDocument().insertString(offset," ></url>");
        }
        model.getCurrentCaret().moveCaretRelatively(1,0,false, false);
        newCompletion(insertionContext.getProject(), insertionContext.getEditor());
    }

    private void newCompletion(Project project, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> new CodeCompletionHandlerBase(CompletionType.BASIC, true, false, true)
                .invokeCompletion(project, editor));
    }
}
