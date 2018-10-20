package ros.integrate.pkt.intention;

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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.ROSPktNameSuggestionProvider;
import ros.integrate.pkt.psi.ROSPktFieldBase;

import java.util.HashSet;
import java.util.Set;

/**
 * a fix used to change duplicate names without triggering a refactor.
 */
public class ChangeNameQuickFix extends BaseIntentionAction {
    private final ROSPktFieldBase parent;
    private final PsiElement badElement;

    public ChangeNameQuickFix(ROSPktFieldBase field, PsiElement element) {
        this.parent = field;
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
        ROSPktNameSuggestionProvider provider = findProvider();
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
                    provider.getSuggestedNames(badElement,parent.getTypeBase(),result);
                    final LookupElement[] items = result
                            .stream()
                            .map(LookupElementBuilder::create)
                            .toArray(LookupElement[]::new);
                    LookupManager.getInstance(project).showLookup(editor, items);
                });
    }

    /**
     * a utility function used to get the provider needed.
     * @return null if the provider does not exist, otherwise the provider itself.
     */
    @Nullable
    private static ROSPktNameSuggestionProvider findProvider() {
        Object[] extensions = Extensions.getExtensions(ROSPktNameSuggestionProvider.EP_NAME);

        for (Object extension : extensions) {
            if (extension instanceof ROSPktNameSuggestionProvider) {
                return (ROSPktNameSuggestionProvider)extension;
            }
        }
        return null;
    }

    /**
     * checks the range of the provided offset based on the document length.
     * @param offset the offset to check
     * @param documentLength the max length of the document to check against.
     * @return the provided offset if it within within allowable range, otherwise the document length.
     */
    @Contract(pure = true)
    private static int getDocumentOffset(int offset, int documentLength) {
        return offset >=0 && offset <= documentLength ? offset : documentLength;
    }
}
