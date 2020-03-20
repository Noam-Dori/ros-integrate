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
import org.jetbrains.annotations.Nullable;

class AttributeNameHandler implements InsertHandler<LookupElement> {
    @Nullable
    private final String forcedAttrName;

    /**
     * @param forcedAttrName leave null if the user may select an attribute.
     *                       Otherwise, the string used is inserted as the attribute.
     */
    AttributeNameHandler(@Nullable String forcedAttrName) {
        this.forcedAttrName = forcedAttrName;
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        String tagName = item.getLookupString();
        CaretModel model = context.getEditor().getCaretModel();
        int offset = model.getOffset();
        String nextChar = context.getDocument().getText(new TextRange(offset, offset + 1)),
                attrInsert = forcedAttrName == null ? "" : forcedAttrName + "=\"\"";
        if ("\r\n".contains(nextChar)) {
            context.getDocument().insertString(offset, " " + attrInsert + "></" + tagName + ">");
        }
        model.getCurrentCaret().moveCaretRelatively(Math.max(attrInsert.length(), 1), 0, false, false);
        if (forcedAttrName == null) {
            newCompletion(context.getProject(), context.getEditor());
        }
    }

    private static void newCompletion(Project project, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> new CodeCompletionHandlerBase(CompletionType.BASIC, true, false, true)
                .invokeCompletion(project, editor));
    }
}
