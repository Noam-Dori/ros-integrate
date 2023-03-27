package ros.integrate.pkg.xml.completion;

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

/**
 * a completion handler that starts writing the tag value. Triggered from completing a tag name or an attribute value
 */
class OpenTagValue implements InsertHandler<LookupElement> {
    @NotNull
    private final String tagName, data;
    private final boolean inAttr, multiline, newCompletion;

    /**
     * constructs a new handler
     * @param tagName the name of the tag to use when moving to data component.
     * @param inAttr was the completion done in an attribute value?
     * @param data optional text to put in the data. Set to "" to prevent adding any data.
     * @param multiline should the tag be multiline?
     * @param newCompletion trigger a new Completion?
     */
    OpenTagValue(@NotNull String tagName, boolean inAttr, @NotNull String data,
                 boolean multiline, boolean newCompletion) {
        this.tagName = tagName;
        this.data = data;
        this.inAttr = inAttr;
        this.multiline = multiline;
        this.newCompletion = newCompletion;
    }

    /**
     * a convenience constructor with fewer parameters
     * @param tagName the name of the tag to use when moving to data component.
     * @param inAttr was the completion done in an attribute value?
     */
    OpenTagValue(@NotNull String tagName, boolean inAttr) {
        this(tagName, inAttr, "", false, false);
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        CaretModel model = context.getEditor().getCaretModel();
        if (inAttr) {
            model.getCurrentCaret().moveCaretRelatively(1, 0, false, false);
        }
        Document doc = context.getDocument();
        int offset = model.getOffset();
        String text = doc.getTextLength() == offset ? "" : doc.getText(new TextRange(offset, offset + 1)),
                whitespace = doc.getText(new TextRange(doc.getLineStartOffset(doc.getLineNumber(offset)),offset))
                        .replaceFirst("[^ ].*",""),
                insert = ">" + (multiline ? "\n    " + whitespace : "") + data +
                        (multiline ? "\n" + whitespace : "") + "</" +
                        (tagName.isEmpty() ? item.getLookupString() : tagName) + ">";
        if ("\r\n".contains(text)) {
            doc.insertString(offset, insert);
        }
        model.getCurrentCaret().moveCaretRelatively(multiline ? 0 : data.isEmpty() ? 1 : insert.length(), 0,
                false, false);
        if (multiline) {
            model.getCurrentCaret().moveCaretRelatively(data.length() + 8, 1, false, false);
        }
        if (newCompletion) {
            newCompletion(context.getProject(), context.getEditor());
        }
    }

    private static void newCompletion(Project project, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> new CodeCompletionHandlerBase(CompletionType.BASIC, true, false, true)
                .invokeCompletion(project, editor));
    }
}
