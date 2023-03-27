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
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

/**
 * a special attribute name handler that is used to skip the attribute section and move to the tag value
 */
class SkipAttribute implements InsertHandler<LookupElement> {
    private final boolean newCompletion;

    /**
     * constructs a new handler that is triggered from "completing" an attribute name
     * @param newCompletion whether to start a new completion
     */
    SkipAttribute(boolean newCompletion) {
        this.newCompletion = newCompletion;
    }


    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        CaretModel model = context.getEditor().getCaretModel();
        int offset = model.getOffset();
        String text = context.getDocument().getText(new TextRange(offset, offset + 1));
        context.getDocument().deleteString(offset - 1, offset);
        if (text.equals(">")) {
            model.getCurrentCaret().moveCaretRelatively(1, 0, false, false);
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
