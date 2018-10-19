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
import ros.integrate.pkt.psi.ROSPktType;

import java.util.ArrayList;
import java.util.List;

/**
 * a class defining the references of {@link ROSPktType} to {@link ROSMsgFile}
 */
public class ROSMsgTypeReference extends PsiReferenceBase<PsiElement> implements PsiFileReference {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    private String key;

    public ROSMsgTypeReference(@NotNull PsiElement element, @NotNull TextRange textRange) { //TODO: namespace support
        super(element, textRange);
        key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<ROSMsgFile> files = ROSMsgUtil.findMessages(project, key, myElement.getContainingFile().getVirtualFile());
        List<ResolveResult> results = new ArrayList<>();
        for (ROSMsgFile file : files) {
            results.add(new PsiElementResolveResult(file));
        }
        return results.toArray(new ResolveResult[0]);
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
        final List<ROSMsgFile> files = ROSMsgUtil.findMessages(project, null, null);
        List<LookupElement> variants = new ArrayList<>();
        for (final ROSMsgFile file : files) {
            String fileName = file.getName();
            if (fileName.length() > 0) {
                variants.add(LookupElementBuilder.create(file).
                        withIcon(ROSIcons.MsgFile).
                        withTypeText(fileName)
                );
            }
        }
        return variants.toArray();
    }
}
