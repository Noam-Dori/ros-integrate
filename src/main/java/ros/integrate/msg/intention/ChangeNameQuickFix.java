package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.ide.DataManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.ROSMsgNameSuggestionProvider;
import ros.integrate.msg.psi.ROSMsgProperty;

import java.util.HashSet;
import java.util.Set;

public class ChangeNameQuickFix extends BaseIntentionAction {
    private final ROSMsgProperty parent;
    private final PsiElement badElement;

    public ChangeNameQuickFix(ROSMsgProperty prop, PsiElement element) {
        this.parent = prop;
        this.badElement = element;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS Message/Service errors";
    }

    @NotNull
    @Override
    public String getText() {
        return "Rename element";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) {
        ROSMsgNameSuggestionProvider provider = findProvider();
        if (badElement == null || provider == null) return;
        DataManager.getInstance()
                .getDataContextFromFocusAsync()
                .onSuccess(context -> {

                    final TextRange textRange = badElement.getTextRange();
                    if (textRange == null) return;
                    final int documentLength = editor.getDocument().getTextLength();
                    final int endOffset = getDocumentOffset(textRange.getEndOffset(), documentLength);
                    final int startOffset = getDocumentOffset(textRange.getStartOffset(), documentLength);
                    editor.getSelectionModel().setSelection(startOffset, endOffset);
                    final String word = editor.getSelectionModel().getSelectedText();

                    if (word == null || StringUtil.isEmpty(word)) {
                        return;
                    }
                    Set<String> result = new HashSet<>();
                    provider.getSuggestedNames(badElement,parent.getType(),result);
                    final LookupElement[] items = result
                            .stream()
                            .map(LookupElementBuilder::create)
                            .toArray(LookupElement[]::new);
                    LookupManager.getInstance(project).showLookup(editor, items);
                });
    }

    @Nullable
    private static ROSMsgNameSuggestionProvider findProvider() {
        Object[] extensions = Extensions.getExtensions(ROSMsgNameSuggestionProvider.EP_NAME);

        for (Object extension : extensions) {
            if (extension instanceof ROSMsgNameSuggestionProvider) {
                return (ROSMsgNameSuggestionProvider)extension;
            }
        }
        return null;
    }

    private static int getDocumentOffset(int offset, int documentLength) {
        return offset >=0 && offset <= documentLength ? offset : documentLength;
    }
}
