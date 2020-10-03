package ros.integrate.pkg.xml.completion;

import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * a completion handler that starts an attribute value. Triggered from completing an attribute name.
 */
class OpenAttributeValue implements InsertHandler<LookupElement> {
    private final boolean newCompletion;

    /**
     * constructs a new handler
     * @param newCompletion whether or not to start a new completion
     */
    OpenAttributeValue(boolean newCompletion) {
        this.newCompletion = newCompletion;
    }


    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        CaretModel model = context.getEditor().getCaretModel();
        int offset = model.getOffset();
        context.getDocument().insertString(offset, "=\"\"");
        model.getCurrentCaret().moveCaretRelatively(2, 0, false, false);
        if (newCompletion) {
            newCompletion(context.getProject(), context.getEditor());
        }
    }

    private static void newCompletion(Project project, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> new CodeCompletionHandlerBase(CompletionType.BASIC, true, false, true)
                .invokeCompletion(project, editor));
    }
}
