package ros.integrate.pkg.xml;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.tree.IElementType;
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
                        if (PackageXmlUtil.getWrapper(parameters.getOriginalFile()) == null) {
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
                        }
                        // XML_NAME: <[url] [type]="website">http://example.com</[url]>
                        // make sure to split these into scenarios
                        // XML_ATTRIBUTE_VALUE_TOKEN: <url type="[website]">http://example.com</url>
                        // XML_DATA_CHARACTERS: <url type="[website]">[http://example.com]</url>
                        // these are also split by space-bars
                    }
                });
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
}
