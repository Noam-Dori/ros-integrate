package ros.integrate.pkt;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
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
        msgName = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
        if(msgName.contains("/")) {
            pkgName = msgName.replaceAll("/.*","");
            msgName = msgName.replaceAll(".*/","");
        } else {
            pkgName = ((ROSPktFile)element.getContainingFile()).getPackage().getName();
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        ROSMsgFile file = ROSPktUtil.findMessage(project, pkgName, msgName);
        if(file == null || file.equals(myElement.getContainingFile())) {
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
        final List<ROSMsgFile> messages = ROSPktUtil.findMessages(project, (ROSMsgFile) myElement.getContainingFile());
        List<LookupElement> variants = new ArrayList<>();
        for (final ROSMsgFile msg : messages) {
            String fileName = msg.getName();
            if (fileName.length() > 0) {
                variants.add(LookupElementBuilder.create(msg).
                        withIcon(ROSIcons.MsgFile).
                        withTypeText(msg.getQualifiedName())
                );
            }
        }
        return variants.toArray();
    }
}
