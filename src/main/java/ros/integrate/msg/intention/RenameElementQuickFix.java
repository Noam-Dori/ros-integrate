package ros.integrate.msg.intention;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.ide.DataManager;
import com.intellij.injected.editor.EditorWindow;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileEditor.impl.text.TextEditorPsiDataProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.actions.RenameElementAction;
import com.intellij.refactoring.rename.NameSuggestionProvider;
import com.intellij.refactoring.rename.RenameHandlerRegistry;
import com.intellij.spellchecker.quickfixes.DictionarySuggestionProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.ROSMsgNameSuggestionProvider;
import ros.integrate.msg.psi.ROSMsgProperty;

import java.util.HashMap;

public class RenameElementQuickFix extends BaseIntentionAction {
    private final ROSMsgProperty parent;
    private final PsiElement badElement;

    public RenameElementQuickFix(ROSMsgProperty prop, PsiElement element) {
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
        if (provider != null) {
            //provider.setActive(true);
        }

        HashMap<String, Object> map = new HashMap<>();
        if (badElement == null) return;
        if (editor == null) return;

        if (editor instanceof EditorWindow) {
            map.put(CommonDataKeys.EDITOR.getName(), editor);
            map.put(CommonDataKeys.PSI_ELEMENT.getName(), badElement);
        } else if (ApplicationManager.getApplication().isUnitTestMode()) { // TextEditorComponent / FiledEditorManagerImpl give away the data in real life
            map.put(
                    CommonDataKeys.PSI_ELEMENT.getName(),
                    new TextEditorPsiDataProvider().getData(CommonDataKeys.PSI_ELEMENT.getName(), editor, editor.getCaretModel().getCurrentCaret())
            );
        }

        final Boolean selectAll = editor.getUserData(RenameHandlerRegistry.SELECT_ALL);
        try {
            editor.putUserData(RenameHandlerRegistry.SELECT_ALL, true);
            DataContext dataContext = SimpleDataContext.getSimpleContext(map, DataManager.getInstance().getDataContext(editor.getComponent()));
            AnAction action = new RenameElementAction();
            AnActionEvent event = AnActionEvent.createFromAnAction(action, null, "", dataContext);
            action.actionPerformed(event);
            if (provider != null) {
                //provider.setActive(false);
            }
        }
        finally {
            editor.putUserData(RenameHandlerRegistry.SELECT_ALL, selectAll);
        }
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
}
