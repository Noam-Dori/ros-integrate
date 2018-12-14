package ros.integrate.pkt;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.psi.ROSMsgFile;
import ros.integrate.pkt.psi.ROSPktFile;

import java.util.ArrayList;
import java.util.List;

/**
 * a class defining the references of {@link ros.integrate.pkt.psi.ROSPktTypeBase} to {@link ROSMsgFile}
 */
public class ROSPktTypeReference extends PsiReferenceBase<PsiElement> implements PsiFileReference {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    private @NotNull String msgName, pkgName;

    public ROSPktTypeReference(@NotNull PsiElement element, @NotNull TextRange textRange) {
        super(element, textRange);
        msgName = element.getText();
        if(msgName.contains("/")) {
            pkgName = msgName.replaceAll("/.*","");
            msgName = msgName.replaceAll(".*/","");
        } else {
            pkgName = ((ROSPktFile)element.getContainingFile().getOriginalFile()).getPackage().getName();
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        ROSMsgFile file = ROSPktUtil.findMessage(project, pkgName, msgName);
        if(file == null || file.equals(myElement.getContainingFile().getOriginalFile())) {
            return ResolveResult.EMPTY_ARRAY;
        }
        return new ResolveResult[]{new PsiElementResolveResult(file)};
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiFile containingFile = myElement.getContainingFile().getOriginalFile();
        final List<ROSMsgFile> messages = ROSPktUtil.findMessages(project, containingFile instanceof ROSMsgFile ? (ROSMsgFile) containingFile : null );
        List<LookupElement> variants = new ArrayList<>();
        for (final ROSMsgFile msg : messages) {
            String fileName = msg.getName();
            if (fileName.length() > 0) {
                variants.add(LookupElementBuilder.create(msg)
                        .withIcon(ROSIcons.MsgFile)
                        .withTypeText(msg.getQualifiedName())
                        .withInsertHandler((context,item) -> addPackageName(context, msg))
                );
            }
        }
        return variants.toArray();
    }

    private void addPackageName(@NotNull InsertionContext context, @NotNull ROSMsgFile msg) {
        CaretModel model = context.getEditor().getCaretModel();

        model.getCurrentCaret().moveCaretRelatively(- msg.getName().length(),0,false,false);
        context.getDocument().insertString(model.getOffset(),msg.getPackage().getName() + "/");
        model.getCurrentCaret().moveCaretRelatively(msg.getQualifiedName().length(),0,false,false);
    }
}
