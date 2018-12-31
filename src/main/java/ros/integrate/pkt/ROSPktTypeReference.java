package ros.integrate.pkt;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSMsgFile;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSPktTypeBase;

import java.util.ArrayList;
import java.util.List;

/**
 * a class defining the references of {@link ros.integrate.pkt.psi.ROSPktTypeBase} to {@link ROSMsgFile}
 */
public class ROSPktTypeReference extends PsiReferenceBase<PsiElement> implements PsiFileReference {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    @NotNull
    private String msgName, pkgName;
    private boolean explicitPackage;

    public ROSPktTypeReference(@NotNull ROSPktTypeBase element, @NotNull TextRange textRange) {
        super(element, textRange);
        msgName = element.raw().getText();
        if (msgName.contains("/")) {
            pkgName = msgName.replaceAll("/.*", "");
            msgName = msgName.replaceAll(".*/", "");
            explicitPackage = true;
        } else {
            pkgName = ((ROSPktFile) element.getContainingFile().getOriginalFile()).getPackage().getName();
            explicitPackage = false;
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        ROSMsgFile file = ROSPktUtil.findMessage(project, pkgName, msgName);
        if (file == null || file.equals(myElement.getContainingFile().getOriginalFile())) {
            return ResolveResult.EMPTY_ARRAY;
        }
        return new ResolveResult[]{new PsiElementResolveResult(file)};
    }

    @Nullable
    @Override
    public ROSMsgFile resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? (ROSMsgFile) resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        PsiFile containingFile = myElement.getContainingFile().getOriginalFile();
        ROSMsgFile thisAsExclude = containingFile instanceof ROSMsgFile ? (ROSMsgFile) containingFile : null;
        final List<ROSMsgFile> messages;
        if(explicitPackage) {
            messages = ROSPktUtil.findMessages(project, pkgName, thisAsExclude);
        } else {
            messages = ROSPktUtil.findMessages(project, null, thisAsExclude);
        }
        List<LookupElement> variants = new ArrayList<>();
        for (final ROSMsgFile msg : messages) {
            String fileName = msg.getPacketName();
            if (fileName.length() > 0) {
                variants.add(LookupElementBuilder.createWithIcon(msg)
                        .withPresentableText(msg.getPacketName())
                        .withTypeText(msg.getQualifiedName())
                        .withInsertHandler((context, item) -> addPackageName(context, msg))
                );
            }
        }
        return variants.toArray();
    }

    private void addPackageName(@NotNull InsertionContext context, @NotNull ROSMsgFile msg) {
        CaretModel model = context.getEditor().getCaretModel();

        context.getDocument().deleteString(model.getOffset() - 4,model.getOffset());

        model.getCurrentCaret().moveCaretRelatively(-msg.getPacketName().length(), 0, false, false);
        // remove prev. package references if they exist
        context.getDocument().deleteString(model.getVisualLineStart(), model.getOffset());
        // add new ref.
        if (!msg.getPackage().getName().equals(((ROSPktFile) context.getFile()).getPackage().getName())) {
            context.getDocument().insertString(model.getOffset(), msg.getPackage().getName() + "/");
        }
        model.getCurrentCaret().moveCaretRelatively(msg.getQualifiedName().length(), 0, false, false);
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        // check if reference is in the same package as target.
        ROSMsgFile referencer = (ROSMsgFile) myElement.getContainingFile().getOriginalFile();
        String pkg, pkt;
        if (newElementName.contains("/")) {
            pkg = newElementName.replaceAll("/.*", "");
            pkt = newElementName.replaceAll(".*/", "");
        } else {
            pkg = pkgName;
            pkt = newElementName;
        }
        if (referencer.getPackage().getName().equals(pkg)) {
            return super.handleElementRename(pkt);
        } else {
            return super.handleElementRename(pkg + "/" + pkt);
        }
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if(!(element instanceof ROSMsgFile)) {
            throw new IncorrectOperationException("Cannot bind to " + element);
        }
        return handleElementRename(((ROSMsgFile)element).getQualifiedName());
    }
}
